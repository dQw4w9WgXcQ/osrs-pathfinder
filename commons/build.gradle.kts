dependencies {
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("net.runelite:runelite-api:${Versions.runelite}")
}

tasks {
    java {
        withSourcesJar()
    }
}