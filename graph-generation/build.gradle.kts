dependencies {
    implementation(project(":commons"))
    implementation(project(":tile-pathfinding"))
    implementation("net.runelite:cache:${Versions.runelite}") {
        exclude(group = "com.google.code", module = "gson")
    }
    implementation("net.runelite:client:${Versions.runelite}") {
        exclude(group = "com.google.code", module = "gson")
    }
    implementation("com.google.code.gson:gson:${Versions.gson}")
    implementation("net.runelite:runelite-api:${Versions.runelite}")
    implementation("commons-cli:commons-cli:1.5.0")
    runtimeOnly("org.slf4j:slf4j-simple:${Versions.slf4j}")
}
