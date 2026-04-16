plugins {
    id("java")
}

group = "com.programacion.avanzada"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}