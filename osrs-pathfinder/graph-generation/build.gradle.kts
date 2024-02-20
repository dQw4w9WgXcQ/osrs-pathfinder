dependencies {
    implementation(project(":commons"))
    implementation("net.runelite:cache:${Versions.runelite}") {
        exclude(group = "com.google.code", module = "gson")
    }
//    implementation("net.runelite:client:${Versions.runelite}") {
//        exclude(group = "com.google.code", module = "gson")
//    }
    implementation("com.google.code.gson:gson:${Versions.gson}")
    implementation("net.runelite:runelite-api:${Versions.runelite}")
    implementation("commons-cli:commons-cli:1.5.0")
    runtimeOnly("org.slf4j:slf4j-simple:${Versions.slf4j}")
}

tasks {
    processResources {
        doLast {
            copy {
                from("${layout.projectDirectory}/src/main/resources/.properties")
                into("${layout.buildDirectory.orNull}/resources/main/")
                filter {
                    it.replace("%VERSION%", project.version.toString())
                }
            }
        }
    }
}
