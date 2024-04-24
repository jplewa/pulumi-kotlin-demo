package myproject

import com.pulumi.core.Output
import com.pulumi.docker.kotlin.Image
import com.pulumi.docker.kotlin.image
import com.pulumi.gcp.Config
import com.pulumi.gcp.artifactregistry.kotlin.repository
import com.pulumi.gcp.container.kotlin.Cluster
import com.pulumi.gcp.container.kotlin.cluster
import com.pulumi.kotlin.Pulumi
import com.pulumi.kubernetes.apps.v1.kotlin.deployment
import com.pulumi.kubernetes.core.v1.kotlin.Namespace
import com.pulumi.kubernetes.core.v1.kotlin.enums.ServiceSpecType
import com.pulumi.kubernetes.core.v1.kotlin.namespace
import com.pulumi.kubernetes.core.v1.kotlin.service
import com.pulumi.kubernetes.kotlin.KubernetesProvider
import com.pulumi.kubernetes.kotlin.kubernetesProvider

private const val NAME = "pulumi-kotlin-demo"

fun main() {
    Pulumi.run { ctx ->
        val image = uploadImage()

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

        val servicePublicIp = service.status?.applyValue { it.loadBalancer?.ingress?.firstOrNull()?.ip }
        ctx.export("servicePublicIp", servicePublicIp)
    }
}

private suspend fun uploadImage(): Image {
    val repository = repository("$NAME-repository") {
        args {
            repositoryId(NAME)
            format("docker")
        }
    }

    val imageUrl = Output.format(
        "%s-docker.pkg.dev/%s/$NAME/$NAME-server",
        repository.location,
        repository.project
    )

    val image = image("$NAME-image") {
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
    return cluster("$NAME-cluster") {
        args {
            deletionProtection(false)
            initialNodeCount(2)
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
            val project = gcpConfig.project().orElseThrow()
            val timeZone = gcpConfig.zone().orElseThrow()
            val context = "${project}_${timeZone}_${name}"
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
) = deployment("$NAME-deployment") {
    args {
        metadata {
            namespace(namespace.id)
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
) = service("$NAME-service") {
    args {
        metadata {
            labels(appLabels)
            namespace(namespace.id)
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
