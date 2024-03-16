package io.github.mjaroslav.bon2gradle.api.impl

import io.github.mjaroslav.bon2gradle.api.MappingProvider
import io.github.mjaroslav.bon2gradle.data.Bon2Handlers
import net.minecraftforge.gradle.delayed.DelayedFile
import net.minecraftforge.gradle.tasks.ExtractConfigTask
import net.minecraftforge.gradle.user.patch.ForgeUserPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File
import java.io.IOException

@Suppress("unused") // Used by String constant
class FG12MappingProvider : MappingProvider {
    override fun getMappingsName(project: Project): String {
        val ext = project.plugins.getPlugin(ForgeUserPlugin::class.java).extension
        return if (ext.mappingsSet()) {
            "${ext.mappingsChannel}_${ext.mappingsVersion}-${ext.version}"
        } else ""
    }

    override fun getMappings(
        project: Project,
        cacheDir: File,
        handlers: Bon2Handlers
    ): Map<String, String> {
        val location =
            DelayedFile(project, "{MCP_DATA_DIR}", project.plugins.getPlugin(ForgeUserPlugin::class.java)).call()
        if (!location.isDirectory) {
            val task = project.tasks.getByName("genSrgs") as ExtractConfigTask
            try {
                task.doTask()
            } catch (e: IOException) {
                throw GradleException("Error while creating ForgeGradle mappings", e)
            }
        }
        return MappingProvider.loadCSV(
            location.resolve("methods.csv"),
            handlers
        ) + MappingProvider.loadCSV(location.resolve("fields.csv"), handlers)
    }
}
