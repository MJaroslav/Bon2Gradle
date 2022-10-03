package com.github.mjaroslav.bon2gradle.util;

import com.github.parker8283.bon2.data.MappingVersion;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@UtilityClass
public class MappingUtils {
    public MappingVersion getMapping(@NotNull File file, @Nullable String relativeConfPath) {
        val conf = relativeConfPath == null ? file.toPath().resolve("unpacked/conf") : file.toPath().resolve(relativeConfPath);
        return new MappingVersion(file.getName(), conf.toFile());
    }

    public MappingVersion getApiMapping(Project project) {
        return new MappingVersion(Utils.getApiVersion(project), Utils.getApiDirectory(project));
    }
}
