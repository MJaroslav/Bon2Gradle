import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
    java
    `java-gradle-plugin`
    `maven-publish`
}

repositories {
    mavenCentral()
    maven {
        name = "forge"
        url = uri("https://maven.minecraftforge.net/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
}

group = "io.github.mjaroslav"
version = "0.4.0"
base.archivesName.set("Bon2Gradle")

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
    withSourcesJar()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {}
val sharedTestSourceSet = sourceSets.create("sharedTest") {}

val functionalTest by tasks.registering(Test::class) {
    group = "verification"
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    useJUnitPlatform()
}

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

tasks.named("check") {
    if (System.getenv("CI")  == "true")
        dependsOn(functionalTest)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compileOnly(Deps.forgeGradle) {
        isChanging = true
    }
    api(Deps.bon2)
    "sharedTestImplementation"(Deps.forgeGradle) {
        isChanging = true
    }
    "sharedTestImplementation"(Deps.jupiter)
    compileOnly(Deps.jetbrainsAnnotations)
    "sharedTestCompileOnly"(Deps.jetbrainsAnnotations)
    testImplementation(sourceSets.getByName("sharedTest").output)
    "functionalTestImplementation"(sourceSets.getByName("sharedTest").output)
}

gradlePlugin {
    plugins {
        create("bon2gradle") {
            id = "bon2gradle"
            implementationClass = "io.github.mjaroslav.bon2gradle.Bon2GradlePlugin"
        }
    }

}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
