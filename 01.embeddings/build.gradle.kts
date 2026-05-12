plugins {
    id("java")
}

group = "com.programacion.avanzada"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.knuddels:jtokkit:1.1.0")
}

tasks.test {
    useJUnitPlatform()
}