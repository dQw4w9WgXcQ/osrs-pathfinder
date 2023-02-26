dependencies {
    implementation(project(":commons"))
    implementation(project(":pathfinding"))
    implementation("net.runelite:cache:${Versions.runelite}") {
        exclude(group = "com.google.code", module = "gson")
    }
    implementation("com.google.code.gson:gson:${Versions.gson}")
    implementation("net.runelite:runelite-api:${Versions.runelite}")
    implementation("commons-cli:commons-cli:1.5.0")
    implementation("org.jetbrains:annotations:${Versions.jetbrainsAnnotations}")
    compileOnly("org.projectlombok:lombok:${Versions.lombok}")
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    runtimeOnly("org.slf4j:slf4j-simple:${Versions.slf4j}")

    testAnnotationProcessor("org.projectlombok:lombok:${Versions.lombok}")
    testCompileOnly("org.projectlombok:lombok:${Versions.lombok}")
}
