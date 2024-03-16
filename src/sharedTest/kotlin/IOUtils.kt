package io.github.mjaroslav.bon2gradle.test.shared

import org.junit.jupiter.api.Assertions
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class IOUtils {
    companion object {
        @JvmStatic
        fun copyFromResources(resource: String, destination: File) {
            var resolved = resource
            if (!resolved.startsWith("/")) resolved = "/$resolved"
            val stream = IOUtils::class.java.getResourceAsStream(resolved)
            Assertions.assertNotNull(stream, "Resource $resolved not found")
            Files.copy(stream!!, destination.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }

        @JvmStatic
        fun readAllLinesFromResources(resource: String): List<String> {
            var resolved = resource
            if (!resolved.startsWith("/")) resolved = "/$resolved"
            val stream = IOUtils::class.java.getResourceAsStream(resolved)
            Assertions.assertNotNull(stream, "Resource $resolved not found")
            return stream!!.bufferedReader().readLines()
        }

        @JvmStatic
        fun readStringFromResources(resource: String): String =
            readAllLinesFromResources(resource).joinToString(System.lineSeparator())

        @JvmStatic
        fun writeString(path: File, string: String) = path.writeText(string)
    }
}

