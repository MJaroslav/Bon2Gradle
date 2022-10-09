package com.github.mjaroslav.bon2gradle.util;

import org.gradle.api.artifacts.ExternalModuleDependency;
import org.gradle.api.internal.artifacts.dependencies.DefaultSelfResolvingDependency;
import org.gradle.api.internal.file.FileCollectionInternal;
import org.gradle.api.tasks.TaskDependency;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class DeobfuscateDependency extends DefaultSelfResolvingDependency {
    @NotNull
    private final ExternalModuleDependency pure;

    public DeobfuscateDependency(@NotNull FileCollectionInternal source, @NotNull ExternalModuleDependency pure) {
        super(source);
        this.pure = pure;
    }

    @NotNull
    @Override
    public String getName() {
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

    @NotNull
    @Override
    public TaskDependency getBuildDependencies() {
        // Это говно ебаное не работает нихуя сука, че бы блять и как бы я
        // нахуй сюда и в целом не писал блять
        return dependencies -> Collections.emptySet();
    }
}
