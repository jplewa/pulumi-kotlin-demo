package myproject

import com.pulumi.Context
import com.pulumi.core.Output
import com.pulumi.docker.kotlin.Image
import com.pulumi.docker.kotlin.image
import com.pulumi.gcp.Config
import com.pulumi.gcp.artifactregistry.kotlin.repository
import com.pulumi.gcp.container.kotlin.Cluster
import com.pulumi.gcp.container.kotlin.ContainerFunctions
import com.pulumi.gcp.container.kotlin.cluster
import com.pulumi.kotlin.Pulumi
import com.pulumi.kubernetes.apps.v1.kotlin.deployment
import com.pulumi.kubernetes.core.v1.kotlin.Namespace
import com.pulumi.kubernetes.core.v1.kotlin.enums.ServiceSpecType
import com.pulumi.kubernetes.core.v1.kotlin.namespace
import com.pulumi.kubernetes.kotlin.KubernetesProvider
import com.pulumi.kubernetes.kotlin.kubernetesProvider

private const val NAME = "pulumi-kotlin-demo"

fun main() {
    Pulumi.run { ctx ->
        val image = uploadImage(ctx)

        val cluster = createCluster()

        val kubeconfig = createKubeconfig(cluster)

        val kubernetesProvider = kubernetesProvider(NAME) {
            args {
                kubeconfig(kubeconfig)
            }
        }
        val namespace = namespace(NAME) {
            opts {
                provider(kubernetesProvider)
            }
        }

        val appLabels = mapOf("appClass" to NAME)

        createDeployment(namespace, appLabels, image, kubernetesProvider)

        val service = createService(appLabels, namespace, kubernetesProvider)

        val servicePublicIp = service.status?.applyValue { it.loadBalancer?.ingress?.first()?.ip }
        ctx.export("servicePublicIp", servicePublicIp)
    }
}

private suspend fun uploadImage(ctx: Context): Image {
    val repository = repository(NAME) {
        args {
            repositoryId(NAME)
            format("docker")
        }
    }

    val imageUrl = Output.all(repository.location, repository.project)
        .applyValue { (zone, project) -> "$zone-docker.pkg.dev/$project/$NAME/$NAME-server" }

    val image = image("$NAME-server") {
        args {
            build {
                dockerfile("../server/Dockerfile")
                context("../server")
                platform("linux/amd64")
            }
            imageName(imageUrl)
        }
    }
    return image
}

private suspend fun createCluster(): Cluster {
    val engineVersion = ContainerFunctions.getEngineVersions().latestMasterVersion
    return cluster(NAME) {
        args {
            initialNodeCount(2)
            minMasterVersion(engineVersion)
            nodeVersion(engineVersion)
            nodeConfig {
                machineType("n1-standard-1")
                oauthScopes(
                    "https://www.googleapis.com/auth/compute",
                    "https://www.googleapis.com/auth/devstorage.read_only",
                    "https://www.googleapis.com/auth/logging.write",
                    "https://www.googleapis.com/auth/monitoring"
                )
            }
        }
    }
}

private fun createKubeconfig(cluster: Cluster): Output<String> =
    Output.all(cluster.name, cluster.endpoint, cluster.masterAuth.applyValue { it.clusterCaCertificate })
        .applyValue { (name, endpoint, caCertificate) ->
            val gcpConfig = Config()
            val context = "${gcpConfig.project().orElseThrow()}_${gcpConfig.zone().orElseThrow()}_${name}"
            """apiVersion: v1
              |clusters:
              |- cluster:
              |    certificate-authority-data: $caCertificate
              |    server: https://${endpoint}
              |  name: $context
              |contexts:
              |- context:
              |    cluster: $context
              |    user: $context
              |  name: $context
              |current-context: $context
              |kind: Config
              |preferences: {}
              |users:
              |- name: $context
              |  user:
              |    exec:
              |      apiVersion: client.authentication.k8s.io/v1beta1
              |      command: gke-gcloud-auth-plugin
              |      installHint: Install gke-gcloud-auth-plugin for use with kubectl by following
              |        https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
              |      provideClusterInfo: true"""
                .trimMargin()
        }

private suspend fun createDeployment(
    namespace: Namespace,
    appLabels: Map<String, String>,
    image: Image,
    kubernetesProvider: KubernetesProvider
) = deployment(NAME) {
    args {
        metadata {
            namespace(namespace.metadata.applyValue { it.name })
            labels(appLabels)
        }
        spec {
            replicas(2)
            selector {
                matchLabels(appLabels)
            }
            template {
                metadata {
                    labels(appLabels)
                }
                spec {
                    containers {
                        name(NAME)
                        image(image.imageName)
                        ports {
                            name("http")
                            containerPort(8080)
                        }
                    }
                }
            }
        }
    }
    opts {
        provider(kubernetesProvider)
    }
}

private suspend fun createService(
    appLabels: Map<String, String>,
    namespace: Namespace,
    kubernetesProvider: KubernetesProvider
) = com.pulumi.kubernetes.core.v1.kotlin.service(NAME) {
    args {
        metadata {
            labels(appLabels)
            namespace(namespace.metadata.applyValue { it.name })
        }
        spec {
            type(ServiceSpecType.LoadBalancer)
            ports {
                port(8080)
                targetPort("http")
            }
            selector(appLabels)
        }
    }
    opts {
        provider(kubernetesProvider)
    }
}
