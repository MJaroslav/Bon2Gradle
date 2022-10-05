package com.github.mjaroslav.bon2gradle.api;

import com.github.parker8283.bon2.data.MappingVersion;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface MappingProvider {
    @Nullable MappingVersion getCurrentMappings(@NotNull Project project, @NotNull File cacheDir);

    default boolean isShouldCreateCacheDir() {
        return false;
    }
}
