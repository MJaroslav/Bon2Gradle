package com.github.mjaroslav.bon2gradle.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@UtilityClass
public class TestUtils {
    public void copyFromResources(@NotNull String resource, @NotNull File destination) throws IOException {
        if (!resource.startsWith("/")) resource = "/" + resource;
        Files.copy(Objects.requireNonNull(TestUtils.class.getResourceAsStream(resource)), destination.toPath(),
            StandardCopyOption.REPLACE_EXISTING);
    }
}
