plugins {
    java
}

group = "github.dqw4w9wgxcq.pathfinder"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.runelite.net")
    }
}

apply<MavenPublishPlugin>()

dependencies {
    implementation("com.google.guava:guava:23.2-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.runelite:runelite-api:1.8.26")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.jetbrains:annotations:23.0.0")
    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testRuntimeOnly("org.slf4j:slf4j-simple:1.7.25")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        withSourcesJar()
    }

    test {
        useJUnitPlatform()
    }
}

configure<PublishingExtension> {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
        }
    }
}
