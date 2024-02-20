allprojects {
    group = "dev.dqw4w9wgxcq.pathfinder"
    version = "1"
}

plugins {
    java
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()

    repositories {
        mavenCentral()
        maven {
            url = uri("https://repo.runelite.net")
        }
    }

    dependencies {
        val lombok = "1.18.30"

        implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
        implementation("org.jetbrains:annotations:23.0.0")
        implementation("org.jspecify:jspecify:0.3.0")
        compileOnly("org.projectlombok:lombok:$lombok")
        annotationProcessor("org.projectlombok:lombok:$lombok")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        testRuntimeOnly("org.slf4j:slf4j-simple:${Versions.slf4j}")
        testCompileOnly("org.projectlombok:lombok:$lombok")
        testAnnotationProcessor("org.projectlombok:lombok:$lombok")
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

    configure<PublishingExtension> {
        publications {
            register("mavenJava", MavenPublication::class) {
                from(components["java"])
            }
        }
    }
}