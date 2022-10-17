package io.github.mjaroslav.bon2gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class Bon2GradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.extensions.create(EXTENSION_NAME, Bon2GradleExtension::class.java, project)
    }
}
