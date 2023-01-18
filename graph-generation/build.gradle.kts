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

dependencies {
    val runelite = "1.8.26"
    val lombok = "1.18.24"
    implementation(project(":graph"))
    implementation("net.runelite:cache:$runelite") {
        exclude(group = "com.google.code", module = "gson")
    }
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.runelite:runelite-api:$runelite")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.jetbrains:annotations:23.0.0")
    compileOnly("org.projectlombok:lombok:$lombok")
    annotationProcessor("org.projectlombok:lombok:$lombok")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    testAnnotationProcessor("org.projectlombok:lombok:$lombok")
    testCompileOnly("org.projectlombok:lombok:$lombok")
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    test {
        useJUnitPlatform()

    }
}