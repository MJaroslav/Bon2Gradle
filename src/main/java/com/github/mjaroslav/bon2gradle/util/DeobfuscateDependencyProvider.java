package com.github.mjaroslav.bon2gradle.util;

import com.github.mjaroslav.bon2gradle.Bon2GradleConstants;
import com.github.parker8283.bon2.BON2Impl;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.SelfResolvingDependency;
import org.gradle.api.internal.file.FileCollectionInternal;
import org.gradle.api.internal.provider.AbstractMinimalProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DeobfuscateDependencyProvider extends AbstractMinimalProvider<SelfResolvingDependency> {
    private final Project project;
    private final Object identifier;

    private Set<File> resolved;
    private ExternalModuleDependency dependency;

    @NotNull
    private Set<File> resolveDirty() {
        if (resolved == null)
            resolved = resolvePure(identifier).stream().map(this::deobfFile).collect(Collectors.toSet());
        return resolved;
    }

    // MEGA THANKS
    // https://stackoverflow.com/questions/66562384/how-to-manually-download-file-from-maven-repository-in-gradle
    @NotNull
    private Set<File> resolvePure(@NotNull Object identifier) {
        val name = "resolveArtifact-" + UUID.randomUUID();
        val configuration = project.getConfigurations().create(name);
        val dependency = project.getDependencies().add(name, identifier);
        if (!(dependency instanceof ExternalModuleDependency))
            throw new IllegalStateException("Bon2Gradle can deobf only ExternalModuleDependence!");
        this.dependency = (ExternalModuleDependency) dependency;
        val result = project.getConfigurations().getByName(name).getResolvedConfiguration();
        if (result.hasError()) result.rethrowFailure();
        return result.getResolvedArtifacts().stream().filter(art -> art.getClassifier() == null ||
            "".equals(art.getClassifier())).map(ResolvedArtifact::getFile).collect(Collectors.toSet());
    }

    @SneakyThrows
    @NotNull
    private File deobfFile(@NotNull File inFile) {
        val parent = new File(project.getBuildDir(), Bon2GradleConstants.DIR_DEOBF);
        val outFile = new File(parent, inFile.getName());
        if ((parent.isDirectory() || parent.mkdirs()) &&
            (!outFile.isFile() || project.getGradle().getStartParameter().isRefreshDependencies())) {
            val bonHandlers = new BonHandlers(project);
            BON2Impl.remap(inFile, outFile, MappingUtils.getCurrentMapping(project),
                bonHandlers, bonHandlers);
        }
        return outFile;
    }

    @NotNull
    @Override
    protected Value<? extends SelfResolvingDependency> calculateOwnValue(@NotNull ValueConsumer consumer) {
        return Value.of(new DeobfuscateDependency((FileCollectionInternal) project.getLayout()
            .files(resolveDirty().toArray()), dependency));
    }

    @Nullable
    @Override
    public Class<SelfResolvingDependency> getType() {
        return SelfResolvingDependency.class;
    }
}
