package myproject

import com.pulumi.kotlin.Pulumi

private const val NAME = "pulumi-kotlin-demo"
private val APP_LABELS = mapOf("appClass" to NAME)

fun main() {
    Pulumi.run { ctx ->
        // TODO #1: create an artifact registry repository
        // TODO #2: create a kubernetes cluster
        // TODO #3: construct an image url, format: "<zone>-docker.pkg.dev/<project-id>/<repository-name>/<image-name>"
        // TODO #4: create an image
        // TODO #5: create a kubeconfig
        //        """apiVersion: v1
        //                |clusters:
        //                |- cluster:
        //                |    certificate-authority-data: <caCertificate>
        //                |    server: https://<endpoint>
        //                |  name: <context>
        //                |contexts:
        //                |- context:
        //                |    cluster: <context>
        //                |    user: <context>
        //                |  name: <context>
        //                |current-context: <context>
        //                |kind: Config
        //                |preferences: {}
        //                |users:
        //                |- name: <context>
        //                |  user:
        //                |    exec:
        //                |      apiVersion: client.authentication.k8s.io/v1beta1
        //                |      command: gke-gcloud-auth-plugin
        //                |      installHint: Install gke-gcloud-auth-plugin for use with kubectl by following
        //                |        https://cloud.google.com/blog/products/containers-kubernetes/kubectl-auth-changes-in-gke
        //                |      provideClusterInfo: true""".trimMargin()
        // TODO #6: create a kubernetes provider
        // TODO #7: create a kubernetes namespace
        // TODO #8: create a kubernetes deployment
        // TODO #9: create a kubernetes load-balancing service

        //        ctx.export("repositoryCreateTime", repository.createTime)
        //        ctx.export("clusterName", cluster.name)
        //        ctx.export("imageUrl", imageUrl)
        //        ctx.export("imageDigest", image.repoDigest)
        //
        //        ctx.export("namespaceStatus", namespace.status?.applyValue { it.phase })
        //        ctx.export("deploymentStatus", deployment.status?.applyValue { it.availableReplicas })
        //
        //        val servicePublicIp = service.status?.applyValue { it.loadBalancer?.ingress?.firstOrNull()?.ip }
        //        ctx.export("servicePublicIp", servicePublicIp)
    }
}
