package com.github.mjaroslav.bon2gradle.test.functional;

import com.github.mjaroslav.bon2gradle.test.shared.IOUtils;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.gradle.testkit.runner.GradleRunner;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;

import static com.github.mjaroslav.bon2gradle.test.shared.TestConstants.PACKAGE;
import static org.junit.jupiter.api.Assertions.assertTrue;

@UtilityClass
public class GradleUtils {
    public String getClasspathString(@NotNull GradleRunner runner) {
        return runner.getPluginClasspath().stream().map(file -> "'" + file.getAbsolutePath() + "'")
            .collect(Collectors.joining(","));
    }

    public void initBaseForgeGradle12Project(@NotNull File file) throws IOException {
        val runner = GradleRunner.create();
        IOUtils.copyFromResources(PACKAGE + "BaseForgeGradle12Project.gradle", new File(file, "build.gradle").toPath());
        runner.forwardOutput();
        runner.withArguments("genSrgs");
        runner.withProjectDir(file);
        val result = runner.build();
        assertTrue(result.getOutput().contains("BUILD SUCCESS"));
    }
}
