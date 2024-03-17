package io.github.mjaroslav.bon2gradle.api

import io.github.mjaroslav.bon2gradle.Bon2GradleExtension
import io.github.mjaroslav.bon2gradle.data.Bon2Handlers
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import java.io.File
import java.nio.charset.StandardCharsets

interface MappingProvider {
    companion object {
        /**
         * Helper function for loading CSV like files.
         */
        @JvmStatic
        fun loadCSV(file: File, handlers: Bon2Handlers): Map<String, String> {
            try {
                val lines = file.readLines(StandardCharsets.UTF_8).drop(1).filter { it.isNotBlank() }
                handlers.start(lines.size - 1, "Loading mappings from CSV file $file")
                val result = mutableMapOf<String, String>()
                var split: List<String>
                for ((index, line) in lines.withIndex()) {
                    handlers.setProgress(index)
                    split = line.split(",")
                    if (split.size > 1) result[split[0]] = split[1]
                    else handlers.handleError("Found bad line in $file: $line", true)
                }
                return result
            } catch (e: Throwable) {
                handlers.handleError("Error while loading CSV file $file", false)
                throw GradleException("Error while creating mappings", e)
            }
        }

        /**
         * Manually execute task with dependencies. Can be helpful for [getMappings].
         */
        @JvmStatic
        fun executeTask(task: Task) {
            task.taskDependencies.getDependencies(task).forEach { executeTask(it) }
            task.actions.forEach { it.execute(task) }
        }
    }

    /**
     * Mappings name. This function result has a higher priority than "[getMappings]" function result.
     *
     * Mapping name format: `channel_version-gameVersion`, for example `stable_12-1.7.10`
     *
     * If you want use mapping provided by "[getMappings]" function, just set this function result to empty String.
     *
     * Pay attention, that using of this function will be download mapping files from remote repository if this it's not cached.
     *
     * Artifacts deobfuscated by this mappings will be "pure" and stored in global Gradle cache subdirectory if "[Bon2GradleExtension.dirty]" property not `true`.
     */
    fun getMappingsName(project: Project): String

    /**
     * Mappings in <Obfuscated, Deobfuscated> format. Yoy can use [loadCSV] for parsing CSV files.
     *
     * This function result has a lower priority than "[getMappingsName]" function result.
     */
    fun getMappings(project: Project, cacheDir: File, handlers: Bon2Handlers): Map<String, String>
}
