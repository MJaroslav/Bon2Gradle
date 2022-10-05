package com.github.mjaroslav.bon2gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

public final class Bon2GradlePlugin implements Plugin<Project> {
    @Override
    public void apply(@NotNull Project project) {
        project.afterEvaluate(project1 -> {
            if (project1.getState().getFailure() != null) return;
            project.getExtensions().create(Bon2GradleConstants.EXTENSION_NAME, Bon2GradleExtension.class, this);
        });
    }
}
