package com.github.mjaroslav.bon2gradle.test.functional;

import lombok.experimental.UtilityClass;
import org.gradle.testkit.runner.GradleRunner;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

@UtilityClass
public class GradleUtils {
    public String getClasspathString(@NotNull GradleRunner runner) {
        return runner.getPluginClasspath().stream().map(file -> "'" + file.getAbsolutePath() + "'")
            .collect(Collectors.joining(","));
    }
}
