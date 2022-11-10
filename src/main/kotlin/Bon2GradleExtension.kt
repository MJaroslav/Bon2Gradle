package io.github.mjaroslav.bon2gradle

import io.github.mjaroslav.bon2gradle.api.MappingProvider
import io.github.mjaroslav.bon2gradle.provider.DeobfuscatedDependencyProvider
import org.gradle.api.GradleScriptException
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class Bon2GradleExtension @Inject constructor(private val project: Project) {
    val forceMapping: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)
    val forcedMappingLocation: RegularFileProperty = project.objects.fileProperty()
    val forcedMappingRelativeConfPath: Property<String> = project.objects.property(String::class.java).convention("")
    val mappingProvider: Property<String> =
        project.objects.property(String::class.java).convention(DEFAULT_MAPPING_PROVIDER)
    val useParallelDeobfuscation: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)

    val mappingProviderObject: MappingProvider? by lazy {
        val result: MappingProvider?
        val className = mappingProvider.get()
        if (className.isBlank()) project.logger.info("MappingProvider not provided")
        try {
            result = Class.forName(className).getConstructor().newInstance() as MappingProvider
        } catch (e: Exception) {
            throw GradleScriptException("Error while mapping provider instantiation", e)
        }
        result
    }

    fun deobf(identifier: Any): DeobfuscatedDependencyProvider =
        DeobfuscatedDependencyProvider(project, identifier)
}
