package com.github.mjaroslav.bon2gradle;

import com.github.mjaroslav.bon2gradle.api.MappingProvider;
import com.github.mjaroslav.bon2gradle.util.MappingUtils;
import com.github.parker8283.bon2.BON2Impl;
import com.github.parker8283.bon2.cli.CLIErrorHandler;
import com.github.parker8283.bon2.cli.CLIProgressListener;
import lombok.val;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.UUID;


public abstract class Bon2GradleExtension {
    private final Project project;

    public abstract Property<Boolean> getForceMapping();

    public abstract RegularFileProperty getForcedMappingLocations();

    public abstract Property<String> getForcedMappingRelativeConfPath();

    public abstract Property<String> getMappingProvider();

    public Bon2GradleExtension(@NotNull Project project) {
        this.project = project;
        getForceMapping().convention(false);
        getMappingProvider().convention("com.github.mjaroslav.bon2gradle.api.impl.FG12Provider");
    }

    @Internal
    @Nullable
    public MappingProvider getMappingProviderObject() {
        try {
            val clazz = Class.forName(getMappingProvider().get());
            return (MappingProvider) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public FileCollection deobf(@NotNull Object identifier) throws IOException {
        val inFile = dirtyResolve(identifier);
        val parent = new File(project.getBuildDir(), Bon2GradleConstants.DIR_DEOBF);
        val outFile = new File(parent, inFile.getName());

        if ((parent.isDirectory() || parent.mkdirs()) &&
            (!outFile.isFile() || project.getGradle().getStartParameter().isRefreshDependencies()))
            BON2Impl.remap(inFile, outFile, MappingUtils.getCurrentMapping(project),
                new CLIErrorHandler(), new CLIProgressListener());

        return project.files(outFile);
    }

    // MEGA THANKS
    // https://stackoverflow.com/questions/66562384/how-to-manually-download-file-from-maven-repository-in-gradle
    private @NotNull File dirtyResolve(@NotNull Object identifier) {
        val name = "resolveArtifact-" + UUID.randomUUID();
        project.getConfigurations().create(name);
        project.getDependencies().add(name, identifier);
        val result = project.getConfigurations().getByName(name).getResolvedConfiguration()
            .getResolvedArtifacts().stream().findFirst();
        if (result.isPresent())
            return result.get().getFile();
        throw new NullPointerException(String.format("Can't resolve %s dependency artifact", identifier));
    }
}
