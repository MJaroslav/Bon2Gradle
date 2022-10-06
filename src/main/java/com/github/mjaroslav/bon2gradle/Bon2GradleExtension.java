package com.github.mjaroslav.bon2gradle;

import com.github.mjaroslav.bon2gradle.api.MappingProvider;
import lombok.val;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.jetbrains.annotations.Nullable;

public abstract class Bon2GradleExtension {
    public abstract Property<Boolean> getForceMapping();

    public abstract RegularFileProperty getForcedMappingLocations();

    public abstract Property<String> getForcedMappingRelativeConfPath();

    public abstract Property<String> getMappingProvider();

    public Bon2GradleExtension() {
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
}
