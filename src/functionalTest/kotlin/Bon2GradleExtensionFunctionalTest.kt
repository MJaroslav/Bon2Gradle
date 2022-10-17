package io.github.mjaroslav.bon2gradle

import io.github.mjaroslav.bon2gradle.test.functional.getClasspathString
import io.github.mjaroslav.bon2gradle.test.shared.PACKAGE
import io.github.mjaroslav.bon2gradle.test.shared.readStringFromResources
import io.github.mjaroslav.bon2gradle.test.shared.writeString
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Bon2GradleExtensionFunctionalTest {
    lateinit var projectDir: Path
    lateinit var buildScript: Path

    @BeforeAll
    fun beforeAll(@TempDir projectDir: Path) {
        this.projectDir = projectDir
        buildScript = projectDir.resolve("build.gradle")
    }

    @Test
    fun `test$deobf751`() {
        deobf("7.5.1") // Current version
    }

    @Test
    fun `test$deobf68`() {
        deobf("6.8") // Minimal version
    }

    private fun deobf(version: String) {
        val runner = GradleRunner.create()
        runner.withPluginClasspath()
        var script: String = readStringFromResources(PACKAGE + "Bon2GradleExtensionBuild.gradle")
        script = String.format(script, getClasspathString(runner))
        writeString(buildScript, script)
        runner.forwardOutput()
        runner.withGradleVersion(version)
        runner.withArguments("dependencies", "--stacktrace")
        runner.withProjectDir(projectDir.toFile())
        val result = runner.build()
        Assertions.assertTrue(result.output.contains("BUILD SUCCESS"), "Run not successful")
        Assertions.assertTrue(
            result.output.contains("curse.maven:hardcore-ender-expansion-228015:2316923"),
            "Library not resolved"
        )
    }
}
