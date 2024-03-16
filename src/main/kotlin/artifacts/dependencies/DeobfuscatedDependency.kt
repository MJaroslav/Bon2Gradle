package io.github.mjaroslav.bon2gradle.artifacts.dependencies

import io.github.mjaroslav.bon2gradle.Bon2GradleExtension
import io.github.mjaroslav.bon2gradle.RESOLVE_CONFIGURATION_PREFIX
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.FileCollectionDependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.artifacts.dependencies.SelfResolvingDependencyInternal
import org.gradle.api.tasks.TaskDependency
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DeobfuscatedDependency(
    private val project: Project,
    private val pure: ExternalModuleDependency
) :
    FileCollectionDependency,
    SelfResolvingDependencyInternal { // Man, why are you demanding about not using Internal things but fails with class cast exception?
    private var reason: String? = null

    private val dirtyVersion: String by lazy {
        val version = pure.version ?: "unspecified"
        val ext = project.extensions.getByType(Bon2GradleExtension::class.java)
        "${version}_${ext.getMappingsSuffix()}"
    }

    @Suppress("USELESS_ELVIS") // WTF man, compiler told me different information about it...
    override fun getGroup(): String = pure.group ?: "unspecified"

    override fun getName(): String = pure.name

    override fun getVersion(): String = dirtyVersion

    override fun contentEquals(dependency: Dependency): Boolean =
        dependency is DeobfuscatedDependency && dependency.deobfuscatedFiles == deobfuscatedFiles

    override fun copy(): DeobfuscatedDependency = DeobfuscatedDependency(project, pure)

    override fun getReason(): String? = reason

    override fun because(reason: String?) {
        this.reason = reason
    }

    override fun getBuildDependencies(): TaskDependency = deobfuscatedFiles.buildDependencies

    override fun resolve(): MutableSet<File> = deobfuscatedFiles.files

    override fun resolve(transitive: Boolean): MutableSet<File> = deobfuscatedFiles.files

    override fun getTargetComponentId(): ComponentIdentifier? = null

    override fun getFiles(): FileCollection = deobfuscatedFiles

    private val pureArtifacts: Set<ResolvedArtifact> by lazy {
        val configuration = project.configurations.create(RESOLVE_CONFIGURATION_PREFIX + UUID.randomUUID())
        project.dependencies.add(configuration.name, pure)
        val resolved = configuration.resolvedConfiguration
        if (resolved.hasError()) resolved.rethrowFailure()
        val result = resolved.resolvedArtifacts
        if (result.count { it.file.isFile } != result.size)
            throw GradleException("One or more pure artifacts for deobfuscation not resolved: $pure")
        project.configurations.remove(configuration) // Not sure about needing in this
        result.toSet()
    }

    private val deobfuscatedFiles: FileCollection by lazy {
        if (project.extensions.getByType(Bon2GradleExtension::class.java).useParallelDeobfuscation.get()) {
            val resolvedPure = pureArtifacts.toTypedArray()
            val deobfuscated = arrayOfNulls<File>(resolvedPure.size)
            val es = Executors.newCachedThreadPool()
            for (i in 0..resolvedPure.size) es.execute {
                deobfuscated[i] = deobfArtifact(resolvedPure[i], true)
            }
            es.shutdown()
            try {
                val finished = es.awaitTermination(1, TimeUnit.MINUTES)
                if (!finished) throw GradleException("Deobfuscation timeout of $pure")
                project.layout.files(deobfuscated)
            } catch (e: InterruptedException) {
                throw GradleException("Can't deobfuscate artifacts from $pure", e)
            }
        } else
            project.layout.files(pureArtifacts.map { deobfArtifact(it) })
    }

    private fun deobfArtifact(
        artifact: ResolvedArtifact,
        parallel: Boolean = false
    ): File {
        val ext = project.extensions.getByType(Bon2GradleExtension::class.java)
        val component = artifact.id.componentIdentifier as ModuleComponentIdentifier
        val parent =
            ext.getCacheDir().resolve(component.group).resolve(component.module).resolve(component.version)
        val deobfFile = parent.resolve(artifact.file.name)
        val exists = deobfFile.isFile
        if (!exists || project.gradle.startParameter.isRefreshDependencies) {
            if (exists) project.logger.info("Found cached deobfuscated resource, but it will be force deobfuscate by --refresh-dependencies: $deobfFile")
            ext.deobfuscator.remap(artifact.file, deobfFile, parallel = parallel)
        } else project.logger.info("Found cached deobfuscated resource: $deobfFile")
        return deobfFile
    }
}
