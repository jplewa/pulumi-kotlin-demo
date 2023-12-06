plugins {
    application
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.virtuslab:pulumi-kubernetes-kotlin:4.1.1.1")
    implementation("org.virtuslab:pulumi-gcp-kotlin:6.64.0.1")
    implementation("org.virtuslab:pulumi-docker-kotlin:4.3.0.1")
}

application {
    mainClass.set(
        project.findProperty("mainClass") as? String ?: "myproject.AppKt"
    )
}
