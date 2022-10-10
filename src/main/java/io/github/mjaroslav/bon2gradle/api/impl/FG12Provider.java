package io.github.mjaroslav.bon2gradle.api.impl;

import io.github.mjaroslav.bon2gradle.api.MappingProvider;
import io.github.mjaroslav.bon2.data.MappingVersion;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraftforge.gradle.delayed.DelayedFile;
import net.minecraftforge.gradle.tasks.ExtractConfigTask;
import net.minecraftforge.gradle.user.patch.ForgeUserPlugin;
import net.minecraftforge.gradle.user.patch.UserPatchExtension;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class FG12Provider implements MappingProvider {
    @SneakyThrows
    @Override
    public @NotNull MappingVersion getCurrentMappings(@NotNull Project project, @NotNull File cacheDir) {
        val forge = project.getPlugins().getPlugin(ForgeUserPlugin.class);
        val extension = forge.getExtension();
        val version = forge.resolve("{API_VERSION}", project, extension);
        val location = new DelayedFile(project, "{MCP_DATA_DIR}", forge).call();
        if (!location.isDirectory()) createMappings(forge, extension, project);
        return new MappingVersion(version, location);
    }

    private void createMappings(@NotNull ForgeUserPlugin forge, @NotNull UserPatchExtension extension,
                                @NotNull Project project) {
        // IT'S NOT FUCKING genSrgs!!!!
        val task = ((ExtractConfigTask) project.getTasks().getByName("extractUserDev"));
        try {
            // I'm not sure in SneakyThrows using :woozy_face:
            task.doTask();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
