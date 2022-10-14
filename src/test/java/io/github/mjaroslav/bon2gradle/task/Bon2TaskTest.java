package io.github.mjaroslav.bon2gradle.task;

import io.github.mjaroslav.bon2gradle.test.shared.IOUtils;
import io.github.mjaroslav.bon2gradle.test.shared.JarComparison;
import lombok.val;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static io.github.mjaroslav.bon2gradle.test.shared.TestConstants.PACKAGE;

class Bon2TaskTest {
    @TempDir
    static File projectDirFile;

    static Path projectDir, deobfuscatedJar, deobfuscatedActualJar, obfuscatedJar, fieldsScv, methodsCsv;

    @BeforeAll
    static void beforeAll() {
        projectDir = projectDirFile.toPath();
        deobfuscatedJar = projectDir.resolve("Deobfuscated.jar");
        deobfuscatedActualJar = projectDir.resolve("DeobfuscatedActual.jar");
        obfuscatedJar = projectDir.resolve("Obfuscated.jar");
        methodsCsv = projectDir.resolve("methods.csv");
        fieldsScv = projectDir.resolve("fields.csv");
    }

    @Test
    void test$deobf() throws IOException {
        val project = ProjectBuilder.builder()
            .withProjectDir(projectDirFile)
            .build();
        val task = project.getTasks().create("deobf", Bon2Task.class);

        IOUtils.copyFromResources(PACKAGE + "task/Bon2TaskObfuscated.jar", deobfuscatedJar);
        IOUtils.copyFromResources(PACKAGE + "task/Bon2TaskDeobfuscated.jar", deobfuscatedActualJar);
        IOUtils.copyFromResources(PACKAGE + "task/Bon2TaskMethods.csv", methodsCsv);
        IOUtils.copyFromResources(PACKAGE + "task/Bon2TaskFields.csv", fieldsScv);

        task.getFrom().set(obfuscatedJar.toFile());
        task.getTo().set(deobfuscatedActualJar.toFile());
        task.getMapping().set(projectDir.toFile());
        task.getMappingsRelativeConfPath().set(".");
        task.doTask();

        JarComparison.compareJarClassMembers(deobfuscatedJar, deobfuscatedActualJar);
    }
}
