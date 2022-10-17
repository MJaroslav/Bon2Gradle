package io.github.mjaroslav.bon2gradle.task

import io.github.mjaroslav.bon2.BON2Impl
import io.github.mjaroslav.bon2.data.MappingVersion
import io.github.mjaroslav.bon2gradle.deobf.Bon2ProgressListenerErrorHandler
import io.github.mjaroslav.bon2gradle.deobf.getCurrentMapping
import io.github.mjaroslav.bon2gradle.deobf.getMapping
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

abstract class Bon2Task : DefaultTask() {
    @get:InputFile
    val from: RegularFileProperty = project.objects.fileProperty()

    @get:OutputFile
    val to: RegularFileProperty = project.objects.fileProperty()

    @get:InputFile @Optional
    val mapping: RegularFileProperty = project.objects.fileProperty()

    @get:Input @Optional
    val mappingRelativeConfPath: Property<String> = project.objects.property(String::class.java)

    @Internal
    fun getMappingVersion(): MappingVersion {
        return if (!mapping.isPresent) getCurrentMapping(project)
        else getMapping(mapping.get().asFile, mappingRelativeConfPath.get())
    }

    @TaskAction
    fun doTask() {
        val bonHandlers = Bon2ProgressListenerErrorHandler(project.logger)
        val parent = to.get().asFile.parentFile
        if (parent.isDirectory || parent.mkdirs())
            BON2Impl.remap(from.get().asFile, to.get().asFile, getMappingVersion(), bonHandlers, bonHandlers)
    }
}
