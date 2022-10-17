package io.github.mjaroslav.bon2gradle.test.shared

import org.junit.jupiter.api.Assertions
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.writeText

class Kekw

fun copyFromResources(resource: String, destination: Path) {
    var resolved = resource
    if (!resolved.startsWith("/")) resolved = "/$resolved"
    val stream = Kekw::class.java.getResourceAsStream(resolved)
    Assertions.assertNotNull(stream, "Resource $resolved not found")
    Files.copy(stream!!, destination, StandardCopyOption.REPLACE_EXISTING)
}

fun readAllLinesFromResources(resource: String): List<String> {
    var resolved = resource
    if (!resolved.startsWith("/")) resolved = "/$resolved"
    val stream = Kekw::class.java.getResourceAsStream(resolved)
    Assertions.assertNotNull(stream, "Resource $resolved not found")
    return stream!!.bufferedReader().readLines()
}

fun readStringFromResources(resource: String): String =
    readAllLinesFromResources(resource).joinToString(System.lineSeparator())

fun writeString(path: Path, string: String) = path.writeText(string)

