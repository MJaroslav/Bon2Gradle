package com.github.mjaroslav.bon2gradle;

import com.github.mjaroslav.bon2gradle.api.MappingProvider;
import com.github.mjaroslav.bon2gradle.util.DeobfuscateDependencyProvider;
import lombok.val;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.github.mjaroslav.bon2gradle.Bon2GradleConstants.DEFAULT_MAPPING_PROVIDER;


public abstract class Bon2GradleExtension {
    @NotNull
    private final Project project;

    private boolean providerInitialized;
    private MappingProvider provider;

    public abstract Property<Boolean> getForceMapping();

    public abstract RegularFileProperty getForcedMappingLocations();

    public abstract Property<String> getForcedMappingRelativeConfPath();

    public abstract Property<String> getMappingProvider();

    public Bon2GradleExtension(@NotNull Project project) {
        this.project = project;
        getForceMapping().convention(false);
        getMappingProvider().convention(DEFAULT_MAPPING_PROVIDER);
    }

    @Internal
    @Nullable
    public MappingProvider getMappingProviderObject() {
        if (providerInitialized) return provider;
        try {
            val clazz = Class.forName(getMappingProvider().get());
            provider = (MappingProvider) clazz.getConstructor().newInstance();
            providerInitialized = true;
        } catch (Exception e) {
            provider = null;
            providerInitialized = true;
        }
        return provider;
    }

    public DeobfuscateDependencyProvider deobf(@NotNull Object identifier) {
        return new DeobfuscateDependencyProvider(project, identifier);
    }
}
