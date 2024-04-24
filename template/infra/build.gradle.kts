plugins {
    application
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.virtuslab:pulumi-kubernetes-kotlin:4.9.1.0")
    implementation("org.virtuslab:pulumi-gcp-kotlin:7.11.1.0")
    implementation("org.virtuslab:pulumi-docker-kotlin:4.5.3.0")
}

application {
    mainClass.set(
        project.findProperty("mainClass") as? String ?: "myproject.AppKt"
    )
}
