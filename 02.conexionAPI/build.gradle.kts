plugins {
    id("java")
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {

    var langchain4jVersion = "1.14.0"

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("dev.langchain4j:langchain4j:$langchain4jVersion")
    implementation("dev.langchain4j:langchain4j-open-ai:${langchain4jVersion}")

    implementation("dev.langchain4j:langchain4j-embeddings:1.14.0-beta24")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:1.14.0-beta24")

    implementation("org.slf4j:slf4j-simple:2.0.17")
}

tasks.test {
    useJUnitPlatform()
}