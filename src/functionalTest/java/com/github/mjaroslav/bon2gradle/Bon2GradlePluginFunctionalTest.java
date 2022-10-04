/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.github.mjaroslav.bon2gradle;

import com.github.mjaroslav.bon2gradle.util.TestUtils;
import lombok.val;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Bon2GradlePluginFunctionalTest {
    @TempDir
    File projectDir;

    private File getBuildFile() {
        return new File(projectDir, "build.gradle");
    }

    private File getSettingsFile() {
        return new File(projectDir, "settings.gradle");
    }

    @Test
    void test$canRunTask() throws IOException {
        val runner = GradleRunner.create();
        runner.withPluginClasspath();
        writeString(getSettingsFile(), "");
        writeString(getBuildFile(), String.format("""
            buildscript {
                repositories {
                    mavenCentral()
                    maven {
                        name 'forge'
                        url 'https://maven.minecraftforge.net/'
                    }
                }
                dependencies {
                    classpath('com.anatawa12.forge:ForgeGradle:1.2-1.0.+') {
                        changing = true
                    }
                    classpath files(%s)
                }
            }
                        
            apply plugin: 'java'
            apply plugin: 'forge'
            apply plugin: 'bon2gradle'
                        
            minecraft {
                version = "1.7.10-10.13.4.1614-1.7.10"
            }
            """, TestUtils.getClasspathString(runner)));
        runner.forwardOutput();
        runner.withArguments("dependencies");
        runner.withProjectDir(projectDir);
        val result = runner.build();
        assertTrue(result.getOutput().contains("BUILD SUCCESS"));
    }

    private void writeString(File file, String string) throws IOException {
        try (val writer = new FileWriter(file)) {
            writer.write(string);
        }
    }
}
