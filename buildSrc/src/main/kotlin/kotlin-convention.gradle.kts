import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    java
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    maven {
        name = "MinecraftForge"
        url = uri("https://maven.minecraftforge.net/")
    }
    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {}
val sharedTestSourceSet = sourceSets.create("sharedTest") {}

gradlePlugin.testSourceSets(functionalTestSourceSet)
gradlePlugin.testSourceSets(sharedTestSourceSet)

configurations["sharedTestImplementation"].extendsFrom(configurations["implementation"])
// Adds all dependencies from sharedTest to test and functionalTest
configurations.filter { conf -> conf.name.startsWith("sharedTest") }.forEach {
    if (!it.name.endsWith("ForTest")) {
        configurations["functionalT${it.name.substring(7)}"].extendsFrom(it)
        configurations["t${it.name.substring(7)}"].extendsFrom(it)
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    withSourcesJar()
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

tasks.register<Test>("functionalTest") {
    group = "verification"
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    systemProperty("test.local.repo", rootProject.layout.buildDirectory.dir("functional-test-repository").get().asFile.absolutePath)
    systemProperty("test.version", rootProject.version.toString())
    useJUnitPlatform()
    dependsOn("publishAllPublicationsToFunctionalTestRepository")
    mustRunAfter("test")
}

tasks.named("check") {
    if (System.getenv("CI") == "true")
        dependsOn("functionalTest")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    api(Dependencies.bon2)

    compileOnly(Dependencies.forgeGradle) {
        isChanging = true
    }
    "sharedTestImplementation"(Dependencies.forgeGradle) {
        isChanging = true
    }

    "sharedTestImplementation"(Dependencies.jupiter)

    testImplementation(sharedTestSourceSet.output)
    "functionalTestImplementation"(sharedTestSourceSet.output)
}

gradlePlugin {
    plugins {
        create("bon2gradle") {
            id = "bon2gradle"
            implementationClass = "io.github.mjaroslav.bon2gradle.Bon2GradlePlugin"
        }
    }
}
