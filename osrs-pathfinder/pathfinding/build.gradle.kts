dependencies {
    implementation(project(":commons"))
    implementation("com.google.code.gson:gson:${Versions.gson}")
    implementation("net.runelite:runelite-api:${Versions.runelite}")
}

tasks {
    java {
        withSourcesJar()
    }
}
