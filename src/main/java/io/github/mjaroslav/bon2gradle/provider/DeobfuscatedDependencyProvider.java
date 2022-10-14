package io.github.mjaroslav.bon2gradle.provider;

import io.github.mjaroslav.bon2.BON2Impl;
import io.github.mjaroslav.bon2.data.MappingVersion;
import io.github.mjaroslav.bon2gradle.Bon2GradleConstants;
import io.github.mjaroslav.bon2gradle.Bon2GradleExtension;
import io.github.mjaroslav.bon2gradle.artifacts.dependencies.DeobfuscatedDependency;
import io.github.mjaroslav.bon2gradle.deobf.Bon2ProgressListenerErrorHandler;
import io.github.mjaroslav.bon2gradle.deobf.MappingUtils;
import io.github.mjaroslav.bon2gradle.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.gradle.api.GradleException;
import org.gradle.api.IllegalDependencyNotation;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.internal.file.FileCollectionInternal;
import org.gradle.api.internal.provider.AbstractMinimalProvider;
import org.gradle.internal.resolve.ArtifactResolveException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DeobfuscatedDependencyProvider extends AbstractMinimalProvider<DeobfuscatedDependency> {
    private final @NotNull Project project;
    private final @NotNull Object identifier;

    private Set<File> resolved;
    private ExternalModuleDependency dependency;

    private @NotNull Set<File> resolveDirty() {
        if (resolved == null) {
            if (project.getExtensions().getByType(Bon2GradleExtension.class).getUseParallelDeobfuscation().get()) {
                val resolvedPure = resolvePure(identifier).toArray(new File[0]);
                val deobfuscated = new File[resolvedPure.length];
                val es = Executors.newCachedThreadPool();
                for (var i = 0; i < resolvedPure.length; i++) {
                    val I = i;
                    es.execute(() -> deobfuscated[I] = deobfFile(resolvedPure[I]));
                }
                es.shutdown();
                try {
                    boolean finished = es.awaitTermination(1, TimeUnit.MINUTES);
                    if (!finished) throw new GradleException("Deobfuscation timeout of " + identifier);
                    resolved = Arrays.stream(deobfuscated).collect(Collectors.toSet());
                } catch (InterruptedException e) {
                    throw new ArtifactResolveException("Can't deobfuscate artifacts from " + identifier, e);
                }
            } else
                resolved = resolvePure(identifier).stream().map(this::deobfFile).collect(Collectors.toSet());
        }
        return resolved;
    }

    // MEGA THANKS
    // https://stackoverflow.com/questions/66562384/how-to-manually-download-file-from-maven-repository-in-gradle
    private @NotNull Set<File> resolvePure(@NotNull Object identifier) {
        val name = "resolveArtifact-" + UUID.randomUUID();
        val configuration = project.getConfigurations().create(name);
        val dependency = project.getDependencies().add(name, identifier);
        if (dependency instanceof ExternalModuleDependency external) {
            this.dependency = external;
            val resolved = project.getConfigurations().getByName(name).getResolvedConfiguration();
            if (resolved.hasError()) resolved.rethrowFailure();
            val result = resolved.getResolvedArtifacts().stream()
                .filter(art -> StringUtils.isEmpty(art.getClassifier())).map(ResolvedArtifact::getFile)
                .collect(Collectors.toSet());
            if (result.isEmpty() || result.stream().filter(File::isFile).count() != result.size())
                throw new ArtifactResolveException("Artifact for deobfuscation not resolved: " + identifier);
            return result;
        } else throw new IllegalDependencyNotation("Bon2Gradle can work only with external module dependencies, " +
            "but your: " + identifier);
    }

    @Contract("_ -> new")
    private @NotNull File getCachesDir(@NotNull MappingVersion mapping) {
        var cachesDir = project.getGradle().getGradleUserHomeDir().getAbsolutePath().replace('\\', '/') + "/caches";
        cachesDir = cachesDir + String.format(Bon2GradleConstants.DIR_DEOBF, mapping.getVersion());
        return new File(cachesDir);
    }

    @SneakyThrows
    private @NotNull File deobfFile(@NotNull File inFile) {
        val mapping = MappingUtils.getCurrentMapping(project);
        val parent = getCachesDir(mapping);
        // TODO: May be make it with group paths?
        val outFile = new File(parent, inFile.getName());
        if ((parent.isDirectory() || parent.mkdirs()) &&
            (!outFile.isFile() || project.getGradle().getStartParameter().isRefreshDependencies())) {
            val bonHandlers = new Bon2ProgressListenerErrorHandler(project);
            BON2Impl.remap(inFile, outFile, mapping,
                bonHandlers, bonHandlers);
        }
        return outFile;
    }

    @Override
    protected @NotNull Value<? extends DeobfuscatedDependency> calculateOwnValue(@NotNull ValueConsumer consumer) {
        return Value.of(new DeobfuscatedDependency((FileCollectionInternal) project.getLayout()
            .files(resolveDirty().toArray()), dependency));
    }

    @Override
    public @Nullable Class<DeobfuscatedDependency> getType() {
        return DeobfuscatedDependency.class;
    }
}
