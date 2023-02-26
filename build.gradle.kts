allprojects {
    group = "dev.dqw4w9wgxcq.pathfinder"
    version = Versions.project
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
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
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