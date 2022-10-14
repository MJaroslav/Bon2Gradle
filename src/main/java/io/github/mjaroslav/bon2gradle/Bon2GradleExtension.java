package io.github.mjaroslav.bon2gradle;

import io.github.mjaroslav.bon2gradle.api.MappingProvider;
import io.github.mjaroslav.bon2gradle.provider.DeobfuscatedDependencyProvider;
import io.github.mjaroslav.bon2gradle.util.LazyNullable;
import io.github.mjaroslav.bon2gradle.util.StringUtils;
import lombok.val;
import org.gradle.api.GradleScriptException;
import org.gradle.api.Project;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Internal;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class Bon2GradleExtension {
    private final @NotNull Project project;

    public abstract Property<Boolean> getForceMapping();

    public abstract RegularFileProperty getForcedMappingLocation();

    public abstract Property<String> getForcedMappingRelativeConfPath();

    public abstract Property<String> getMappingProvider();

    public abstract Property<Boolean> getUseParallelDeobfuscation();

    public Bon2GradleExtension(@NotNull Project project) {
        this.project = project;
        getForceMapping().convention(false);
        getMappingProvider().convention(Bon2GradleConstants.DEFAULT_MAPPING_PROVIDER);
        getUseParallelDeobfuscation().convention(false);
    }

    private final LazyNullable<MappingProvider> mappingProviderObject = new LazyNullable<>(() -> {
        val className = getMappingProvider().get();
        if (StringUtils.isEmpty(className)) {
            Bon2GradlePlugin.getLogger().info("MappingProvider not provided");
            return null;
        }
        try {
            return (MappingProvider) Class.forName(className).getConstructor().newInstance();
        } catch (Exception e) {
            throw new GradleScriptException("Error while mapping provider instantiation", e);
        }
    });

    @Internal
    public @Nullable MappingProvider getMappingProviderObject() {
        return mappingProviderObject.get();
    }

    @Contract("_ -> new")
    public DeobfuscatedDependencyProvider deobf(@NotNull Object identifier) {
        return new DeobfuscatedDependencyProvider(project, identifier);
    }
}
