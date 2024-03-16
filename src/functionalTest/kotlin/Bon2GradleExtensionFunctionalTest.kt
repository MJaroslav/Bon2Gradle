package io.github.mjaroslav.bon2gradle

import io.github.mjaroslav.bon2gradle.test.functional.GradleUtils
import io.github.mjaroslav.bon2gradle.test.shared.IOUtils
import io.github.mjaroslav.bon2gradle.test.shared.PACKAGE
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.TempDir
import java.io.File

// TODO: Replace this by independent of Minecraft tests and add tests for FG12 provider instead
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Bon2GradleExtensionFunctionalTest {
    private lateinit var projectDir: File
    private lateinit var buildScript: File

    @BeforeAll
    fun beforeAll(@TempDir projectDir: File) {
        this.projectDir = projectDir
        buildScript = projectDir.resolve("build.gradle")
    }

    @Test
    fun deobf85() {
        deobf("8.5") // Current version
    }

    @Test
    fun deobf751() {
        deobf("7.5.1") // Previous version
    }

    @Test
    fun deobf68() {
        deobf("6.8") // Minimal version
    }

    private fun deobf(version: String) {
        val runner = GradleRunner.create()
        runner.withPluginClasspath()
        var script: String = IOUtils.readStringFromResources(PACKAGE + "Bon2GradleExtensionBuild.gradle")
        script = String.format(script, GradleUtils.getClasspathString(runner))
        IOUtils.writeString(buildScript, script)
        runner.forwardOutput()
        runner.withGradleVersion(version)
        runner.withArguments("dependencies", "--stacktrace", "--refresh-dependencies")
        runner.withProjectDir(projectDir)
        val result = runner.build()
        Assertions.assertTrue(result.output.contains("BUILD SUCCESS"), "Run not successful")
        Assertions.assertTrue(
            result.output.contains("curse.maven:hardcore-ender-expansion-228015:2316923_deobfuscated_with_DIRTY"),
            "Library not resolved"
        )
    }
}
