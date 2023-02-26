dependencies {
    implementation("net.runelite:runelite-api:${Versions.runelite}")
}

tasks {
    java {
        withSourcesJar()
    }
}