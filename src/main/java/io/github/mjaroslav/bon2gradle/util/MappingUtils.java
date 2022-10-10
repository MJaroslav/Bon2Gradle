package io.github.mjaroslav.bon2gradle.util;

import io.github.mjaroslav.bon2gradle.Bon2GradleExtension;
import io.github.mjaroslav.bon2.data.MappingVersion;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@UtilityClass
public class MappingUtils {
    @Contract("_, _ -> new")
    public MappingVersion getMapping(@NotNull File file, @Nullable String relativeConfPath) {
        val conf = relativeConfPath == null ? file.toPath().resolve("unpacked/conf") : file.toPath().resolve(relativeConfPath);
        return new MappingVersion(file.getName(), conf.toFile());
    }

    @Contract("_ -> new")
    public File getCacheDir(@NotNull Project project) {
        return new File(project.getBuildDir(), "bon2Gradle/mappings/");
    }

    public MappingVersion getCurrentMapping(@NotNull Project project) {
        val config = project.getExtensions().getByType(Bon2GradleExtension.class);
        if (config.getForceMapping().get()) {
            val forcedLocation = config.getForcedMappingLocation();
            if (forcedLocation.isPresent() && forcedLocation.get().getAsFile().isDirectory())
                return getMapping(forcedLocation.get().getAsFile(), config.getForcedMappingRelativeConfPath().get());
            throw new IllegalStateException("Forced mapping not set!");
        }
        val provider = config.getMappingProviderObject();
        val cacheDir = getCacheDir(project);
        if (provider != null && (cacheDir.isDirectory() || cacheDir.mkdirs()))
            return provider.getCurrentMappings(project, cacheDir);
        throw new IllegalStateException("No one mapping or provider not found or set!");
    }
}
