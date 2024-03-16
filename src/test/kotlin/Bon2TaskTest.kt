package io.github.mjaroslav.bon2gradle.tasks

import io.github.mjaroslav.bon2gradle.Bon2GradlePlugin
import io.github.mjaroslav.bon2gradle.test.shared.IOUtils
import io.github.mjaroslav.bon2gradle.test.shared.JarComparison
import io.github.mjaroslav.bon2gradle.test.shared.PACKAGE
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Bon2TaskTest {
    private lateinit var projectDir: File
    private lateinit var producedJar: File
    private lateinit var deobfuscatedJar: File
    private lateinit var obfuscatedJar: File
    private lateinit var fieldsCsv: File
    private lateinit var methodsCsv: File

    @BeforeAll
    fun beforeAll(@TempDir projectDir: File) {
        this.projectDir = projectDir
        producedJar = projectDir.resolve("produced.jar")
        deobfuscatedJar = projectDir.resolve("deobfuscated.jar")
        obfuscatedJar = projectDir.resolve("obfuscated.jar")
        methodsCsv = projectDir.resolve("methods.csv")
        fieldsCsv = projectDir.resolve("fields.csv")
        IOUtils.copyFromResources("${PACKAGE}tasks/obfuscated.jar", obfuscatedJar)
        IOUtils.copyFromResources("${PACKAGE}tasks/deobfuscated.jar", deobfuscatedJar)
        IOUtils.copyFromResources("${PACKAGE}tasks/methods.csv", methodsCsv)
        IOUtils.copyFromResources("${PACKAGE}tasks/fields.csv", fieldsCsv)
    }

    @BeforeEach
    fun beforeEach() {
        producedJar.delete()
    }

    @Test
    fun bon2task() {
        val project = ProjectBuilder.builder()
            .withProjectDir(projectDir)
            .build()
        project.plugins.apply(Bon2GradlePlugin::class.java)
        val task = project.tasks.create("deobfuscate", Bon2Task::class.java)
        task.pureFile.set(obfuscatedJar)
        task.deobfFile.set(producedJar)
        task.csv.add(project.layout.projectDirectory.file(methodsCsv.name))
        task.csv.add(project.layout.projectDirectory.file(fieldsCsv.name))
        task.doTask()
        JarComparison.compareJarClassMembers(producedJar, deobfuscatedJar)
    }

    @Test
    fun bon2taskReversed() {
        val project = ProjectBuilder.builder()
            .withProjectDir(projectDir)
            .build()
        project.plugins.apply(Bon2GradlePlugin::class.java)
        val task = project.tasks.create("obfuscate", Bon2Task::class.java)
        task.pureFile.set(deobfuscatedJar)
        task.deobfFile.set(producedJar)
        task.csv.add(project.layout.projectDirectory.file(methodsCsv.name))
        task.csv.add(project.layout.projectDirectory.file(fieldsCsv.name))
        task.reverse.set(true)
        task.doTask()
        JarComparison.compareJarClassMembers(producedJar, obfuscatedJar)
    }
}
