package io.github.mjaroslav.bon2gradle.provider

import io.github.mjaroslav.bon2.BON2Impl
import io.github.mjaroslav.bon2gradle.Bon2GradleExtension
import io.github.mjaroslav.bon2gradle.DIR_DEOBF
import io.github.mjaroslav.bon2gradle.artifacts.dependencies.DeobfuscatedDependency
import io.github.mjaroslav.bon2gradle.deobf.Bon2ProgressListenerErrorHandler
import io.github.mjaroslav.bon2gradle.deobf.getCurrentMapping
import org.gradle.api.GradleException
import org.gradle.api.IllegalDependencyNotation
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.internal.file.FileCollectionInternal
import org.gradle.api.internal.provider.AbstractMinimalProvider
import org.gradle.api.internal.provider.ValueSupplier
import org.gradle.internal.resolve.ArtifactResolveException
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class DeobfuscatedDependencyProvider constructor(
    private val project: Project,
    private val identifier: Any
) : AbstractMinimalProvider<DeobfuscatedDependency>() {
    private var resolved: Set<File>? = null
    private lateinit var dependency: ExternalModuleDependency

    override fun getType(): Class<DeobfuscatedDependency> = DeobfuscatedDependency::class.java

    override fun calculateOwnValue(consumer: ValueSupplier.ValueConsumer): ValueSupplier.Value<out DeobfuscatedDependency> =
        ValueSupplier.Value.of(
            DeobfuscatedDependency(
                project.layout
                    .files(resolveDirty().toTypedArray()) as FileCollectionInternal, dependency
            )
        )

    // MEGA THANKS
    // https://stackoverflow.com/questions/66562384/how-to-manually-download-file-from-maven-repository-in-gradle
    private fun resolvePure(): Set<File> {
        val name = "resolveArtifact-" + UUID.randomUUID()
        project.configurations.create(name)
        val dependency = project.dependencies.add(name, identifier)
        if (dependency is ExternalModuleDependency) {
            this.dependency = dependency
            val resolved = project.configurations.getByName(name).resolvedConfiguration
            if (resolved.hasError()) resolved.rethrowFailure()
            val result = resolved.resolvedArtifacts.filter { art -> art.classifier.isNullOrEmpty() }
                .map(ResolvedArtifact::getFile).toSet()
            if (result.isEmpty() || result.count(File::isFile) != result.size)
                throw ArtifactResolveException("Artifact for deobfuscation not resolved: $identifier")
            return result
        } else throw IllegalDependencyNotation("Bon2Gradle can work only with external module dependencies, but your: $identifier")
    }

    private fun getCachesDir(): File {
        val mapping = getCurrentMapping(project)
        return File(
            "${project.gradle.gradleUserHomeDir.absolutePath.replace('\\', '/')}/caches" +
                String.format(DIR_DEOBF, mapping.version)
        )
    }

    private fun deobfFile(inFile: File): File {
        val parent = getCachesDir()
        // TODO: May be make it with group paths?
        val outFile = File(parent, inFile.name)
        if ((parent.isDirectory || parent.mkdirs()) &&
            (!outFile.isFile || project.gradle.startParameter.isRefreshDependencies)
        ) {
            val bonHandlers = Bon2ProgressListenerErrorHandler(project.logger)
            BON2Impl.remap(
                inFile, outFile, getCurrentMapping(project),
                bonHandlers, bonHandlers
            )
        }
        return outFile
    }

    private fun resolveDirty(): Set<File> {
        if (resolved == null) {
            if (project.extensions.getByType(Bon2GradleExtension::class.java).useParallelDeobfuscation.get()) {
                val resolvedPure = resolvePure().toTypedArray()
                val deobfuscated = arrayOfNulls<File>(resolvedPure.size)
                val es = Executors.newCachedThreadPool()
                for (i in 0..resolvedPure.size) es.execute { deobfuscated[i] = deobfFile(resolvedPure[i]) }
                es.shutdown()
                try {
                    val finished = es.awaitTermination(1, TimeUnit.MINUTES)
                    if (!finished) throw GradleException("Deobfuscation timeout of $identifier")
                    resolved = deobfuscated.filterNotNull().toSet()
                } catch (e: InterruptedException) {
                    throw ArtifactResolveException("Can't deobfuscate artifacts from $identifier", e)
                }
            } else
                resolved = resolvePure().map(this::deobfFile).toSet()
        }
        return resolved as Set<File>
    }
}
