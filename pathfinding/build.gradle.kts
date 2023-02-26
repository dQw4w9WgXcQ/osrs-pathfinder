dependencies {
    implementation(project(":commons"))
    implementation("com.google.code.gson:gson:${Versions.gson}")
    implementation("net.runelite:runelite-api:${Versions.runelite}")
    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
    implementation("org.jetbrains:annotations:23.0.0")
    compileOnly("org.projectlombok:lombok:${Versions.lombok}")
    annotationProcessor("org.projectlombok:lombok:${Versions.lombok}")

    testRuntimeOnly("org.slf4j:slf4j-simple:${Versions.slf4j}")
}

tasks {
    java {
        withSourcesJar()
    }
}
