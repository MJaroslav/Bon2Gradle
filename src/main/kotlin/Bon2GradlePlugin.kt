package io.github.mjaroslav.bon2gradle

import io.github.mjaroslav.bon2gradle.tasks.CleanDeobfCacheTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class Bon2GradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create(EXTENSION_NAME, Bon2GradleExtension::class.java, project)

        project.tasks.register("cleanGlobalDeobfCache", CleanDeobfCacheTask::class.java) {
            it.group = PLUGIN_NAME
            it.description = "Delete local directory with deobfuscated artifacts"
            it.global.set(true)
        }
        project.tasks.register("cleanLocalDeobfCache", CleanDeobfCacheTask::class.java) {
            it.group = PLUGIN_NAME
            it.description = "Delete global directory with deobfuscated artifacts"
        }
        project.tasks.register("cleanDeobfCache") {
            it.group = PLUGIN_NAME
            it.description = "Delete local and global directories with deobfuscated artifacts"
            it.dependsOn("cleanLocalDeobfCache", "cleanGlobalDeobfCache")
        }

        project.afterEvaluate { if (ext.resolve.get()) ext.resolveAndDeobfuscate() }
    }
}
