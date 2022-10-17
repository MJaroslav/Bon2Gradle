package io.github.mjaroslav.bon2gradle.deobf

import io.github.mjaroslav.bon2.data.MappingVersion
import io.github.mjaroslav.bon2gradle.Bon2GradleExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File

fun getMapping(file: File, relativeConfPath: String?): MappingVersion =
    MappingVersion(file.name, File(file, relativeConfPath ?: "unpacked/conf"))

fun getCacheDir(project: Project): File =
    File(project.buildDir, "bon2Gradle/mappings/")

fun getCurrentMapping(project: Project): MappingVersion {
    val extension = project.extensions.getByType(Bon2GradleExtension::class.java)
    if (extension.forceMapping.get()) {
        val location = extension.forcedMappingLocation
        if (location.isPresent && location.get().asFile.isDirectory)
            return getMapping(location.get().asFile, extension.forcedMappingRelativeConfPath.get())
        throw GradleException("Forced mapping not set!")
    }
    val provider = extension.mappingProviderObject
    val cacheDir = getCacheDir(project)
    if (provider != null && (cacheDir.isDirectory || cacheDir.mkdirs()))
        return provider.getCurrentMappings(project, cacheDir)
    throw GradleException("Forced mapping or provider not set!")
}
