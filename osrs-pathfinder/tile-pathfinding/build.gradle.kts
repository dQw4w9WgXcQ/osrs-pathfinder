dependencies {
    implementation(project(":commons"))
}

tasks {
    java {
        withSourcesJar()
    }
}
