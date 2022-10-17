package io.github.mjaroslav.bon2gradle.task

import io.github.mjaroslav.bon2gradle.test.shared.PACKAGE
import io.github.mjaroslav.bon2gradle.test.shared.compareJarClassMembers
import io.github.mjaroslav.bon2gradle.test.shared.copyFromResources
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Bon2TaskTest {
    lateinit var projectDir: Path
    lateinit var deobfuscatedJar: Path
    lateinit var deobfuscatedActualJar: Path
    lateinit var obfuscatedJar: Path
    lateinit var fieldsScv: Path
    lateinit var methodsCsv: Path

    @BeforeAll
    fun beforeAll(@TempDir projectDir: Path) {
        this.projectDir = projectDir
        deobfuscatedJar = projectDir.resolve("Deobfuscated.jar")
        deobfuscatedActualJar = projectDir.resolve("DeobfuscatedActual.jar")
        obfuscatedJar = projectDir.resolve("Obfuscated.jar")
        methodsCsv = projectDir.resolve("methods.csv")
        fieldsScv = projectDir.resolve("fields.csv")
    }

    @Test
    fun `test$deobf`() {
        val project = ProjectBuilder.builder()
            .withProjectDir(projectDir.toFile())
            .build()
        val task = project.tasks.create("deobf", Bon2Task::class.java)
        copyFromResources("${PACKAGE}task/Bon2TaskObfuscated.jar", obfuscatedJar)
        copyFromResources("${PACKAGE}task/Bon2TaskDeobfuscated.jar", deobfuscatedJar)
        copyFromResources("${PACKAGE}task/Bon2TaskMethods.csv", methodsCsv)
        copyFromResources("${PACKAGE}task/Bon2TaskFields.csv", fieldsScv)
        task.from.set(obfuscatedJar.toFile())
        task.to.set(deobfuscatedActualJar.toFile())
        task.mapping.set(projectDir.toFile())
        task.mappingRelativeConfPath.set(".")
        task.doTask()
        compareJarClassMembers(deobfuscatedJar, deobfuscatedActualJar)
    }
}
