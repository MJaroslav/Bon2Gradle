package io.github.mjaroslav.bon2gradle.artifacts.dependencies;

import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultSelfResolvingDependency;
import org.gradle.api.internal.file.FileCollectionInternal;
import org.gradle.api.tasks.TaskDependency;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class DeobfuscatedDependency extends DefaultSelfResolvingDependency {
    private final @NotNull ExternalModuleDependency pure;

    public DeobfuscatedDependency(@NotNull FileCollectionInternal source, @NotNull ExternalModuleDependency pure) {
        super(source);
        this.pure = pure;
    }

    @Override
    public @NotNull String getName() {
        return pure.getName();
    }

    @Override
    public String getVersion() {
        return pure.getVersion();
    }

    @Override
    public String getGroup() {
        return pure.getGroup();
    }

    @Override
    public @NotNull TaskDependency getBuildDependencies() {
        // This shit don't work, I can't understand why
        return dependencies -> Collections.emptySet();
    }
}
