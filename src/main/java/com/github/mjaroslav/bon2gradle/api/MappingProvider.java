package com.github.mjaroslav.bon2gradle.api;

import com.github.parker8283.bon2.data.MappingVersion;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@FunctionalInterface
public interface MappingProvider {
    /**
     * This {@link MappingVersion} will be used as default value if forced mapping disabled.
     * MappingVersion constructor takes two parameters: name and directory with methods.csv and fields.csv
     * files. If you want to use directory name as version, please, you only {@link File#getName() simple name}.
     *
     * @param project  current project object for calling another stuff (like forge plugin in default implementation).
     * @param cacheDir directory for storing generated csv files. If your Minecraft Dev plugin don't create csv files,
     *                 you should create them there manually. You no needed in this directory creation.
     * @return new instance of MappingVersion for your Minecraft Dev plugin.
     */
    @Nullable MappingVersion getCurrentMappings(@NotNull Project project, @NotNull File cacheDir);
}
