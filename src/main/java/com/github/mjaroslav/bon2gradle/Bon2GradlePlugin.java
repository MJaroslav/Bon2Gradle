package com.github.mjaroslav.bon2gradle;

import lombok.Getter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import static com.github.mjaroslav.bon2gradle.Bon2GradleConstants.EXTENSION_NAME;

@Getter
public abstract class Bon2GradlePlugin implements Plugin<Project> {
    private Bon2GradleExtension extension;
    private Project project;

    @Override
    public void apply(@NotNull Project project) {
        this.project = project;
        extension = project.getExtensions().create(EXTENSION_NAME, Bon2GradleExtension.class);
        project.afterEvaluate(project1 -> {
            if (project1.getState().getFailure() != null) return;
            setup();
        });
    }

    private void setup() {}
}
