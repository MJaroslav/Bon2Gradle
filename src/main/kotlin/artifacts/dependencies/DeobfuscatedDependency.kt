package io.github.mjaroslav.bon2gradle.artifacts.dependencies

import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.internal.artifacts.dependencies.DefaultSelfResolvingDependency
import org.gradle.api.internal.file.FileCollectionInternal

class DeobfuscatedDependency constructor(source: FileCollectionInternal, private val pure: ExternalModuleDependency) :
    DefaultSelfResolvingDependency(source) {
    override fun getGroup(): String = pure.group ?: "null"

    override fun getName(): String = pure.name

    override fun getVersion(): String = pure.version ?: "null"
}
