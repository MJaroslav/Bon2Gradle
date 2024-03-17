package io.github.mjaroslav.bon2gradle

import com.github.parker8283.bon2.data.IErrorHandler
import com.github.parker8283.bon2.data.IProgressListener
import groovy.lang.Closure
import io.github.mjaroslav.bon2gradle.api.MappingProvider
import io.github.mjaroslav.bon2gradle.api.impl.DefaultMappingProvider
import io.github.mjaroslav.bon2gradle.artifacts.dependencies.DeobfuscatedDependency
import io.github.mjaroslav.bon2gradle.data.Bon2Handlers
import io.github.mjaroslav.bon2gradle.data.GradleLoggerBon2Handlers
import io.github.mjaroslav.bon2gradle.deobf.Deobfuscator
import org.gradle.api.GradleScriptException
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.FileCollectionDependency
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import java.io.File
import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate", "unused") // It's API entities
abstract class Bon2GradleExtension @Inject constructor(private val project: Project) {
    /**
     * Force mappings usage from provided by "[csv]" or "[mappingsName]" properties instead of using "[MappingProvider]" from "[providerClass]" property.
     */
    val force: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)

    /**
     * Always cache deobfuscated artifacts in project build directory and mark it's as "dirty".
     */
    val dirty: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)

    /**
     * Mappings name. This property has a higher priority than "[csv]" property and requires "[force]" property must be `true`.
     *
     * Mapping name format: `channel_version-gameVersion`, for example `stable_12-1.7.10`
     *
     * If you want use mapping provided by [csv] property, just set this property value to empty String.
     *
     * Pay attention, that using of this property will be download mapping files from remote repository if this it's not cached.
     *
     * Artifacts deobfuscated by this mappings will be "pure" and stored in global Gradle cache subdirectory if "[dirty]" property not `true`.
     */
    val mappingsName: Property<String> = project.objects.property(String::class.java)

    /**
     * List of "csv" files of obfuscated to deobfuscated mappings. This property has a lower priority than "[mappingsName]" and requires "[force]" property must be `true`.
     *
     * You don't have to use csv files exactly, just add text files with "`obfuscated_name,deobfuscated_name`" (without quites) lines, but pay attention that first line will be skipped.
     *
     * Artifacts deobfuscated by this mappings will be "dirty" and stored in project build directory.
     */
    val csv: ListProperty<RegularFile> = project.objects.listProperty(RegularFile::class.java)

    /**
     * The fully qualified name of the desired class of [MappingProvider] implementation. This property requires "[force]" property must be `false`.
     *
     * It can be used for providing mappings from your Minecraft Development Gradle Plugin.
     *
     * Default value is an implementation for anatawa12's ForgeGradle 1.2.
     *
     * @see MappingProvider
     */
    val providerClass: Property<String> =
        project.objects.property(String::class.java).convention(DEFAULT_MAPPING_PROVIDER)

    /**
     * Use parallel execution for deobfuscating dependencies with more than one artifact.
     */
    val useParallelDeobfuscation: Property<Boolean> = project.objects.property(Boolean::class.java).convention(false)

    /**
     * Automatically deobfuscate all created by [deobf][Bon2GradleExtension.deobf] function dependencies after project evaluate.
     */
    val resolve: Property<Boolean> = project.objects.property(Boolean::class.java).convention(true)

    /**
     * Merged realization of [IErrorHandler] and [IProgressListener] for BON2.
     */
    val bonHandlers = GradleLoggerBon2Handlers(project.logger).setPercentStep(10).setDisableDoneSpam(true)

    /**
     * Configured by extension parameters [Deobfuscator] for jar files.
     *
     * @see Deobfuscator
     */
    val deobfuscator: Deobfuscator by lazy {
        Deobfuscator(
            mappingProvider.getMappingsName(project),
            mappingProvider.getMappings(project, getMappingCacheDir(), bonHandlers),
            bonHandlers
        )
    }

    /**
     * Lazy initialized by extension parameters [MappingProvider] object. For internal usage, use [deobfuscator] instead.
     */
    val mappingProvider: MappingProvider by lazy {
        if (force.get()) {
            DefaultMappingProvider()
        } else {
            try {
                val className = providerClass.get()
                if (className.isBlank()) throw IllegalArgumentException("MappingProvider not provided")
                Class.forName(className).getConstructor().newInstance() as MappingProvider
            } catch (e: Exception) {
                throw GradleScriptException("Error while mapping provider instantiation", e)
            }
        }
    }


    /**
     * Deobfuscate dependency with parameters from this extension.
     *
     * Pay attention that you should use `flatDir` instead of `files(...)` if you want to use files with this function.
     *
     * @param identifier dependency identifier like as usually
     *
     * @return deobfuscated dependency or obfuscated if failure.
     */
    fun deobf(identifier: Any): Dependency = deobf(project.dependencies.create(identifier))

    /**
     * Deobfuscate dependency with parameters from this extension with provided [Closure].
     *
     * Pay attention that you should use `flatDir` instead of `files(...)` if you want to use files with this function.
     *
     * @param identifier dependency identifier like as usually
     * @param configure configure [Closure] for dependency before deobfuscation
     *
     * @return deobfuscated dependency or obfuscated if failure.
     */
    fun deobf(identifier: Any, configure: Closure<*>): Dependency =
        deobf(project.dependencies.create(identifier, configure))


    /**
     * Deobfuscate dependency with parameters from this extension.
     *
     * Pay attention that you should use `flatDir` instead of `files(...)` if you want to use files with this function.
     *
     * @param dependency dependency for deobfuscation, must be [ExternalModuleDependency] implementation.
     *
     * @return deobfuscated dependency or obfuscated if failure.
     */
    // Dependency type warnings from ForgeGradle6
    fun deobf(dependency: Dependency): Dependency {
        if (dependency is ExternalModuleDependency)
            return DeobfuscatedDependency(project, dependency)
        else if (dependency is FileCollectionDependency)
            project.logger.warn("files(...) dependencies are not deobfuscated. Use flatDir repository instead: https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:flat_dir_resolver")
        project.logger.warn("Cannot deobfuscate dependency of type ${dependency::class.java.simpleName}, using obfuscated version!")
        return dependency
    }

    /**
     * Get suffix for dependency version with mappings name. For "dirty" artifacts will be used "`DIRTY`" mappings name.
     */
    fun getMappingsSuffix(): String {
        val mappings = getMappingsName().ifBlank { "DIRTY" }
        return "deobfuscated_with_$mappings"
    }

    /**
     * Get mappings name or empty string if not presented.
     */
    fun getMappingsName(): String =
        mappingProvider.getMappingsName(project)

    /**
     * Get mappings for "dirty" deobfuscation or empty Map if not presented.
     */
    fun getMappings(handlers: Bon2Handlers): Map<String, String> =
        mappingProvider.getMappings(project, getMappingCacheDir(), handlers)

    /**
     * Return "purity" of mappings. "Pure" mappings it a provided by mappings name value instead of csv files if "[dirty]" property not `true`.
     *
     * @return true if mappings name provided and "[dirty]" property not `true`.
     */
    fun isPureMappings(): Boolean =
        !dirty.get() and mappingProvider.getMappingsName(project).isNotBlank()

    /**
     * Get directory for storing deobfuscated artifacts, its location sensitive of mapping parameters.
     *
     * You can force location to be relative from project build directory by "[dirty]" property.
     *
     * For default, for pure artifacts used subdirectory in global Gradle caches directory.
     */
    fun getCacheDir(): File =
        if (isPureMappings())
            project.gradle.gradleUserHomeDir.resolve("caches").resolve(GLOBAL_CACHE_DIRECTORY)
                .resolve(getMappingsName())
        else
            project.layout.buildDirectory.get().asFile.resolve(LOCAL_CACHE_DIRECTORY)

    /**
     * Get directory for storing mappings and creates it if not exists.
     */
    fun getMappingCacheDir(): File {
        val result = project.layout.buildDirectory.get().asFile.resolve(MAPPING_CACHE_DIRECTORY)
        if (!result.isDirectory) result.mkdirs()
        return result
    }

    /**
     * Manually resolves and deobfuscate all added to any configuration [DeobfuscatedDependency] dependencies.
     *
     * Called automatically if "[resolve]" property is `true`.
     */
    fun resolveAndDeobfuscate() =
        project.configurations.flatMap { it.dependencies }.filterIsInstance<DeobfuscatedDependency>()
            .forEach { it.resolve() }
}
