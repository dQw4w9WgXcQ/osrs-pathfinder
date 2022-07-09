plugins {
    java
}

group = "github.dqw4w9wgxcq"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.runelite.net")
    }
}

dependencies {
    val runeliteVersion = "1.8.26"
    val runeliteLombokVersion = "1.18.24"
    val runeliteSlf4jVersion = "1.7.25"
    implementation("net.runelite:cache:$runeliteVersion")//provides gson 2.8.5, slf4j api 1.7.25, guava 23.2-jre
    implementation("net.runelite:runelite-api:$runeliteVersion")
    implementation("org.slf4j:slf4j-simple:$runeliteSlf4jVersion")
    implementation("commons-cli:commons-cli:1.5.0")

    compileOnly("org.projectlombok:lombok:$runeliteLombokVersion")
    annotationProcessor("org.projectlombok:lombok:$runeliteLombokVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    getByName<Test>("test") {
        useJUnitPlatform()
    }
}