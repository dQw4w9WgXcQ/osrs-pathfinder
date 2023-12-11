dependencies {
    implementation("net.runelite:runelite-api:${Versions.runelite}")
    implementation("com.google.code.gson:gson:${Versions.gson}")
}

tasks {
    java {
        withSourcesJar()
    }
}