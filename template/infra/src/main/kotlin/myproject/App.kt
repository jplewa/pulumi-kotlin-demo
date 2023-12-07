package myproject

import com.pulumi.core.Output
import com.pulumi.docker.kotlin.Image
import com.pulumi.gcp.artifactregistry.kotlin.Repository
import com.pulumi.gcp.container.kotlin.Cluster
import com.pulumi.kotlin.Pulumi
import com.pulumi.kubernetes.apps.v1.kotlin.Deployment
import com.pulumi.kubernetes.core.v1.kotlin.Namespace
import com.pulumi.kubernetes.core.v1.kotlin.Service
import com.pulumi.kubernetes.kotlin.KubernetesProvider

private const val NAME = "pulumi-kotlin-demo"
private val APP_LABELS = mapOf("appClass" to NAME)

fun main() {
    Pulumi.run { ctx ->
        // TODO #1: create an artifact registry repository
        // val repository = createRepository()

        // TODO #2: create a kubernetes cluster
        // val cluster = createCluster()

        // TODO #3: construct an image url
        // val imageUrl = "<zone>-docker.pkg.dev/<project-id>/<repository-name>/<image-name>"

        // TODO #4: create an image
        // val image = createImage(imageUrl)

        // TODO #5: create a kubeconfig
        // val kubeconfig = """
        //       |apiVersion: v1
        //       |clusters:
        //       |- cluster:
        //       |    certificate-authority-data: <caCertificate>
        //       |    server: https://<endpoint>
        //       |  name: $NAME
        //       |contexts:
        //       |- context:
        //       |    cluster: $NAME
        //       |    user: $NAME
        //       |  name: $NAME
        //       |current-context: $NAME
        //       |kind: Config
        //       |preferences: {}
        //       |users:
        //       |- name: $NAME
        //       |  user:
        //       |    exec:
        //       |      apiVersion: client.authentication.k8s.io/v1beta1
        //       |      command: gke-gcloud-auth-plugin
        //       |      installHint: Install gke-gcloud-auth-plugin for use with kubectl by following
        //       |        https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
        //       |      provideClusterInfo: true
        //     """
        //     .trimMargin()

        // TODO #6: create a kubernetes provider
        // val kubernetesProvider = createProvider(kubeconfig)

        // TODO #7: create a kubernetes namespace
        // val namespace = createNamespace(kubernetesProvider)

        // TODO #8: create a kubernetes deployment
        // val deployment = createDeployment(kubernetesProvider, namespace, image)

        // TODO #9: create a kubernetes load-balancing service
        // val service = createLoadBalancingService(kubernetesProvider, namespace)
    }
}

private suspend fun createRepository(): Repository {
    TODO()
}

private suspend fun createCluster(): Cluster {
    TODO()
}

private suspend fun createImage(imageUrl: Output<String>): Image {
    TODO()
}

private suspend fun createProvider(kubeconfig: Output<String>): KubernetesProvider {
    TODO()
}

private suspend fun createNamespace(kubernetesProvider: KubernetesProvider): Namespace {
    TODO()
}

private suspend fun createDeployment(
    kubernetesProvider: KubernetesProvider,
    namespace: Namespace,
    image: Image
): Deployment {
    TODO()
}

private suspend fun createLoadBalancingService(
    kubernetesProvider: KubernetesProvider,
    namespace: Namespace
): Service {
    TODO()
}
