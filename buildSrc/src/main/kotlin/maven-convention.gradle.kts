plugins {
    `maven-publish`
}

afterEvaluate {
    publishing {
        repositories {
            maven {
                name = "JitCI" // JitCI internal repository with fallback
                url = uri(System.getenv("DEPLOY_DIR") ?: layout.buildDirectory.dir("deploy"))
            }
            maven {
                name = "functionalTest"
                url = uri(layout.buildDirectory.dir("functional-test-repository"))
            }
        }
    }
}
