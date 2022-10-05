package com.github.mjaroslav.bon2gradle.api.impl;

import com.github.mjaroslav.bon2gradle.api.MappingProvider;
import com.github.parker8283.bon2.data.MappingVersion;
import lombok.val;
import net.minecraftforge.gradle.delayed.DelayedFile;
import net.minecraftforge.gradle.user.patch.ForgeUserPlugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class FG12Provider implements MappingProvider {
    @Override
    public @NotNull MappingVersion getCurrentMappings(@NotNull Project project, @NotNull File cacheDir) {
        val forge = project.getPlugins().getPlugin(ForgeUserPlugin.class);
        val version = forge.resolve("{API_VERSION}", project, forge.getExtension());
        val location = new DelayedFile(project, "{MCP_DATA_DIR}", forge).call();
        return new MappingVersion(version, location);
    }
}
