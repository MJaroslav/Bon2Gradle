package com.github.mjaroslav.bon2gradle;

import com.github.mjaroslav.bon2gradle.api.MappingProvider;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.gradle.api.tasks.Internal;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@Getter
@Setter
public final class Bon2GradleExtension {
    private boolean forceMapping;

    @Nullable
    private File forcedMappingLocations;

    @Nullable
    private String forcedMappingRelativeConfPath;

    @Nullable
    private String mappingProvider = "com.github.mjaroslav.bon2gradle.api.impl.FG12Provider";

    @Internal
    @Nullable
    public MappingProvider getMappingProviderObject() {
        try {
            val clazz = Class.forName(mappingProvider);
            return (MappingProvider) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
