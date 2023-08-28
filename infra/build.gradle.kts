plugins {
    application
    kotlin("jvm") version "1.9.0"
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("com.pulumi:pulumi:(,1.0]")
    implementation("org.virtuslab:pulumi-kubernetes-kotlin:4.1.1.0")
    implementation("org.virtuslab:pulumi-gcp-kotlin:6.62.0.0")
    implementation("org.virtuslab:pulumi-docker-kotlin:4.3.0.0")
}

application {
    mainClass.set(
        project.findProperty("mainClass") as? String ?: "myproject.AppKt"
    )
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17
