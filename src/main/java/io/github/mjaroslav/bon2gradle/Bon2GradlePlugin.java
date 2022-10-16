package io.github.mjaroslav.bon2gradle;

import lombok.Getter;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.jetbrains.annotations.NotNull;

import static io.github.mjaroslav.bon2gradle.Bon2GradleConstants.EXTENSION_NAME;

@Getter
public class Bon2GradlePlugin implements Plugin<Project> {
    @Getter
    private static Logger logger;

    private Bon2GradleExtension extension;
    private Project project;

    @Override
    public void apply(@NotNull Project project) {
        this.project = project;
        logger = project.getLogger();
        extension = project.getExtensions().create(EXTENSION_NAME, Bon2GradleExtension.class, project);
    }
}
