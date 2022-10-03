package com.github.mjaroslav.bon2gradle.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.minecraftforge.gradle.delayed.DelayedFile;
import net.minecraftforge.gradle.user.patch.ForgeUserPlugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@UtilityClass
public class Utils {
    public ForgeUserPlugin getForge(@NotNull Project project) {
        return project.getPlugins().getPlugin(ForgeUserPlugin.class);
    }

    public String resolveString(@NotNull Project project, @NotNull String pattern) {
        val forge = getForge(project);
        return forge.resolve(pattern, project, forge.getExtension());
    }

    @Contract("_, null -> fail")
    public DelayedFile delayedFile(@NotNull Project project, Object object) {
        val forge = getForge(project);
        if (object instanceof String string) return new DelayedFile(project, string, forge);
        if (object instanceof File file) return new DelayedFile(file);
        throw new IllegalArgumentException("'object' parameter must be only String or File");
    }

    public String getApiVersion(@NotNull Project project) {
        return resolveString(project, "{API_VERSION}");
    }

    public File getApiDirectory(@NotNull Project project) {
        return delayedFile(project, "{MCP_DATA_DIR}").call();
    }
}
