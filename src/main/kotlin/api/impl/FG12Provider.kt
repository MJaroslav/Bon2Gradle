package io.github.mjaroslav.bon2gradle.api.impl

import io.github.mjaroslav.bon2.data.MappingVersion
import io.github.mjaroslav.bon2gradle.api.MappingProvider
import net.minecraftforge.gradle.delayed.DelayedFile
import net.minecraftforge.gradle.tasks.ExtractConfigTask
import net.minecraftforge.gradle.user.patch.ForgeUserPlugin
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File
import java.io.IOException

class FG12Provider : MappingProvider {
    override fun getCurrentMappings(project: Project, cacheDir: File): MappingVersion {
        val forge = project.plugins.getPlugin(ForgeUserPlugin::class.java)
        val version = forge.resolve("{API_VERSION}", project, forge.extension)
        val location = DelayedFile(project, "{MCP_DATA_DIR}", forge).call()
        if (!location.isDirectory) {
            val task = project.tasks.getByName("extractUserDev") as ExtractConfigTask
            try {
                // I'm not sure in SneakyThrows using :woozy_face:
                task.doTask()
            } catch (e: IOException) {
                throw GradleException("Error while creating ForgeGradle mappings", e)
            }
        }
        return MappingVersion(version, location)
    }
}
