plugins {
    application
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.virtuslab:pulumi-kotlin:0.9.4.0")
    implementation("org.virtuslab:pulumi-kubernetes-kotlin:4.1.1.0")
    implementation("org.virtuslab:pulumi-gcp-kotlin:6.62.0.0")
    implementation("org.virtuslab:pulumi-docker-kotlin:4.3.0.0")
}

application {
    mainClass.set(
        project.findProperty("mainClass") as? String ?: "myproject.AppKt"
    )
}
