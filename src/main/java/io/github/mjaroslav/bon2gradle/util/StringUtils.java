package io.github.mjaroslav.bon2gradle.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

@UtilityClass
public class StringUtils {
    public boolean isNull(@Nullable String str) {
        return str == null || "".equals(str);
    }

    public boolean isEmpty(@Nullable String str) {
        return str == null || "".equals(str.trim());
    }

    public boolean isNotNull(@Nullable String str) {
        return str != null && !"".equals(str);
    }

    public boolean isNotEmpty(@Nullable String str) {
        return str != null && !"".equals(str.trim());
    }
}
