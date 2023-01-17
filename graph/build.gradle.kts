plugins {
    java
}

group = "github.dqw4w9wgxcq.pathfinder"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.runelite.net")
    }
}

dependencies {
    implementation("com.google.guava:guava:23.2-jre")
    implementation("com.google.code.gson:gson:2.8.5")
    implementation("net.runelite:runelite-api:1.8.26")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.jetbrains:annotations:23.0.0")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}