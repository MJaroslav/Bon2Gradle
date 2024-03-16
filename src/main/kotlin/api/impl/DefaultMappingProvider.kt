package io.github.mjaroslav.bon2gradle.api.impl

import io.github.mjaroslav.bon2gradle.Bon2GradleExtension
import io.github.mjaroslav.bon2gradle.api.MappingProvider
import io.github.mjaroslav.bon2gradle.data.Bon2Handlers
import org.gradle.api.Project
import java.io.File

class DefaultMappingProvider : MappingProvider {
    override fun getMappingsName(project: Project): String {
        return project.extensions.getByType(Bon2GradleExtension::class.java).mappingsName.get()
    }

    override fun getMappings(
        project: Project,
        cacheDir: File,
        handlers: Bon2Handlers
    ): Map<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        project.extensions.getByType(Bon2GradleExtension::class.java).csv.get()
            .map { MappingProvider.loadCSV(it.asFile, handlers) }.forEach {
                result.putAll(it)
            }
        return result
    }
}
