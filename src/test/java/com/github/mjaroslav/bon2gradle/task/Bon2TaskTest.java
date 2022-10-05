package com.github.mjaroslav.bon2gradle.task;

import com.github.mjaroslav.bon2gradle.util.JarComparison;
import com.github.mjaroslav.bon2gradle.util.TestUtils;
import lombok.val;
import org.gradle.testfixtures.ProjectBuilder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

class Bon2TaskTest {
    @TempDir
    File projectDir;

    @Contract(" -> new")
    private @NotNull File getDeobfuscatedJar() {
        return new File(projectDir, "Deobfuscated.jar");
    }

    @Contract(" -> new")
    private @NotNull File getDeobfuscatedActualJar() {
        return new File(projectDir, "DeobfuscatedActual.jar");
    }

    @Contract(" -> new")
    private @NotNull File getObfuscatedJar() {
        return new File(projectDir, "Obfuscated.jar");
    }

    @Contract(" -> new")
    private @NotNull File getFieldsCsv() {
        return new File(projectDir, "fields.csv");
    }

    @Contract(" -> new")
    private @NotNull File getMethodsCsv() {
        return new File(projectDir, "methods.csv");
    }

    @Test
    void test$deobf() throws IOException {
        val project = ProjectBuilder.builder()
            .withProjectDir(projectDir)
            .build();
        val task = project.getTasks().create("deobf", Bon2Task.class);

        TestUtils.copyFromResources("/com/github/mjaroslav/bon2gradle/task/Bon2TaskObfuscated.jar", getObfuscatedJar());
        TestUtils.copyFromResources("/com/github/mjaroslav/bon2gradle/task/Bon2TaskDeobfuscated.jar", getDeobfuscatedJar());
        TestUtils.copyFromResources("/com/github/mjaroslav/bon2gradle/task/Bon2TaskMethods.csv", getMethodsCsv());
        TestUtils.copyFromResources("/com/github/mjaroslav/bon2gradle/task/Bon2TaskFields.csv", getFieldsCsv());

        task.setFrom(getObfuscatedJar());
        task.setTo(getDeobfuscatedActualJar());
        task.setMappings(projectDir);
        task.setMappingsRelativeConfPath(".");
        task.doTask();

        try (val expectedJarFile = new JarFile(getDeobfuscatedJar());
             val outJarFile = new JarFile(getDeobfuscatedActualJar())) {
            JarComparison.compareJarClassMembers(expectedJarFile, outJarFile);
        }
    }
}
