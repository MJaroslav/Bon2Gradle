package io.github.mjaroslav.bon2gradle.tasks

import io.github.mjaroslav.bon2gradle.GLOBAL_CACHE_DIRECTORY
import io.github.mjaroslav.bon2gradle.LOCAL_CACHE_DIRECTORY
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class CleanDeobfCacheTask : DefaultTask() {
    @get:Input
    @Optional
    val global = project.objects.property(Boolean::class.java).convention(false)

    @TaskAction
    fun doTask() {
        if (global.get()) project.gradle.gradleUserHomeDir.resolve(GLOBAL_CACHE_DIRECTORY).deleteRecursively()
        else project.layout.buildDirectory.get().asFile.resolve(LOCAL_CACHE_DIRECTORY).deleteRecursively()
    }
}
