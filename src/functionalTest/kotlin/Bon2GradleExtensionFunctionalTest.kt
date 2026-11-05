package io.github.mjaroslav.bon2gradle

import io.github.mjaroslav.bon2gradle.test.shared.IOUtils
import io.github.mjaroslav.bon2gradle.test.shared.PACKAGE
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
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
    private lateinit var cacheDir: File
    private lateinit var heeDeobfFile: File

    @BeforeAll
    fun beforeAll(@TempDir projectDir: File) {
        this.projectDir = projectDir
        buildScript = projectDir.resolve("build.gradle")
        cacheDir = projectDir.parentFile.resolve(".testkit")
        heeDeobfFile =
            cacheDir.resolve("caches").resolve("bon2gradle").resolve("stable_12-1.7.10").resolve("curse.maven")
                .resolve("hardcore-ender-expansion-228015").resolve("2316923")
                .resolve("hardcore-ender-expansion-228015-2316923.jar")
    }

    @Test
    fun deobf85() = deobfAll("8.5") // Current version

    @Test
    fun deobf764() = deobfAll("7.6.4") // Latest 7 version

    @Test
    fun deobf751() = deobfAll("7.5.1") // Previous version

    @Test
    fun deobf694() = deobfAll("6.9.4") // Latest 6 version

    @Test
    fun deobf68() = deobfAll("6.8") // Minimal version

    private fun deobfAll(version: String) {
        deobf(version, checkCache = false, refresh = false)
        deobf(version, checkCache = true, refresh = false)
        deobf(version, checkCache = false, refresh = true)
    }

    private fun deobf(version: String, checkCache: Boolean, refresh: Boolean) {
        val runner = GradleRunner.create()
        IOUtils.writeString(buildScript, IOUtils.readStringFromResources(PACKAGE + "Bon2GradleExtensionBuild.gradle"))
        runner.forwardOutput()
        runner.withTestKitDir(cacheDir)
        runner.withGradleVersion(version)
        val args = mutableListOf(
            "dependencies",
            "--stacktrace",
            "--info",
            "-Dtest.local.repo=${System.getProperty("test.local.repo")}",
            "-Dtest.version=${System.getProperty("test.version")}"
        )
        if (refresh) args += "--refresh-dependencies"
        runner.withArguments(args)
        runner.withProjectDir(projectDir)
        val result = runner.build()
        Assertions.assertEquals(SUCCESS, result.task(":dependencies")!!.outcome, "Task execution result not SUCCESS")
        Assertions.assertTrue(
            result.output.contains("curse.maven:hardcore-ender-expansion-228015:2316923_deobfuscated_with_stable_12-1.7.10"),
            "Library not resolved"
        )
        Assertions.assertTrue(heeDeobfFile.isFile, "Deobfuscated artifact not found")
        if (checkCache && !refresh)
            Assertions.assertTrue(
                result.output.contains("Found cached deobfuscated resource: $heeDeobfFile"),
                "Deobfuscated artifact not found in cache"
            )
        if (refresh)
            Assertions.assertTrue(
                result.output.contains("Found cached deobfuscated resource, but it will be force deobfuscate by --refresh-dependencies: $heeDeobfFile"),
                "Deobfuscated artifact not reloaded by --refresh-dependencies"
            )
    }
}
