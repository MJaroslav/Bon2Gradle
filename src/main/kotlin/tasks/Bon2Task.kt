package io.github.mjaroslav.bon2gradle.tasks

import io.github.mjaroslav.bon2gradle.Bon2GradleExtension
import io.github.mjaroslav.bon2gradle.api.MappingProvider
import io.github.mjaroslav.bon2gradle.deobf.Deobfuscator
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*

@Suppress("unused") // API usage
abstract class Bon2Task : DefaultTask() {
    @get:InputFile
    val pureFile: RegularFileProperty = project.objects.fileProperty()

    @get:OutputFile
    val deobfFile: RegularFileProperty = project.objects.fileProperty()

    @get:Input
    @Optional
    val mappingsName: Property<String> = project.objects.property(String::class.java).convention("")

    @get:Input
    @Optional
    val csv: ListProperty<RegularFile> = project.objects.listProperty(RegularFile::class.java)

    @get:Input
    @Optional
    val reverse = project.objects.property(Boolean::class.java).convention(false)

    @get:Internal
    val deobfuscator: Deobfuscator by lazy {
        val ext = project.extensions.getByType(Bon2GradleExtension::class.java)
        val mappings = mutableMapOf<String, String>()
        csv.get().forEach { mappings.putAll(MappingProvider.loadCSV(it.asFile, ext.bonHandlers)) }
        Deobfuscator(mappingsName.get(), mappings, ext.bonHandlers)
    }

    @TaskAction
    fun doTask() =
        deobfuscator.remap(pureFile.get().asFile, deobfFile.get().asFile, reverse = reverse.get())
}
