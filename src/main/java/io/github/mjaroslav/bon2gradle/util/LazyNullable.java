package io.github.mjaroslav.bon2gradle.util;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@RequiredArgsConstructor
public final class LazyNullable<T> {
    private final @NotNull Supplier<T> initializer;
    private boolean initialized;
    private @Nullable T value;

    public @Nullable T get() {
        if (!initialized) {
            value = initializer.get();
            initialized = true;
        }
        return value;
    }
}
