plugins {
    `maven-publish`
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
            }
        }
        repositories {
            maven {
                name = "JitCI" // JitCI internal repository with fallback
                url = uri(System.getenv("DEPLOY_DIR") ?: layout.buildDirectory.dir("deploy"))
            }
        }
    }
}
