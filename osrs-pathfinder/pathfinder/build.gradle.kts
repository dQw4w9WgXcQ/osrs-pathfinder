dependencies {
    implementation(project(":commons"))
    implementation("com.google.code.gson:gson:${Versions.gson}")
    implementation("net.runelite:runelite-api:${Versions.runelite}")
    implementation("redis.clients:jedis:5.1.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation("org.slf4j:slf4j-simple:${Versions.slf4j}")
}

tasks {
    java {
        withSourcesJar()
    }
}
