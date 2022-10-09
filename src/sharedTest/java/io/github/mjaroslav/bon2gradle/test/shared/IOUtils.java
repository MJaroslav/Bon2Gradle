package io.github.mjaroslav.bon2gradle.test.shared;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@UtilityClass
public class IOUtils {
    public void copyFromResources(@NotNull String resource, @NotNull Path destination) throws IOException {
        if (!resource.startsWith("/")) resource = "/" + resource;
        val is = IOUtils.class.getResourceAsStream(resource);
        Assertions.assertNotNull(is, String.format("Resource %s not found", resource));
        Files.copy(Objects.requireNonNull(is), destination,
            StandardCopyOption.REPLACE_EXISTING);
    }

    public List<String> readAllLinesFromResources(@NotNull String resource) {
        if (!resource.startsWith("/")) resource = "/" + resource;
        val is = IOUtils.class.getResourceAsStream(resource);
        Assertions.assertNotNull(is, String.format("Resource %s not found", resource));
        val reader = new BufferedReader(new InputStreamReader(is));
        return reader.lines().collect(Collectors.toList());
    }

    public @NotNull String readStringFromResources(@NotNull String resource) {
        return String.join(System.lineSeparator(), readAllLinesFromResources(resource));
    }

    public void writeString(@NotNull Path path, @NotNull String string) throws IOException {
        Files.write(path, string.getBytes(StandardCharsets.UTF_8));
    }
}
