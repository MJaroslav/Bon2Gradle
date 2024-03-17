package io.github.mjaroslav.bon2gradle.deobf

import com.github.parker8283.bon2.srg.SimpleRemapper
import com.github.parker8283.bon2.util.*
import io.github.mjaroslav.bon2gradle.data.Bon2Handlers
import io.github.mjaroslav.bon2gradle.data.SysErrBon2Handlers
import org.gradle.api.GradleScriptException
import java.io.File
import java.nio.charset.StandardCharsets

class Deobfuscator(
    private val mappingsName: String,
    private val mappings: Map<String, String>,
    private val defaultHandlers: Bon2Handlers = SysErrBon2Handlers()
) {
    fun remap(
        inJar: File,
        outJar: File,
        reverse: Boolean = false,
        handlers: Bon2Handlers = defaultHandlers,
        parallel: Boolean = false
    ) {
        if (!outJar.parentFile.isDirectory) outJar.parentFile.mkdirs()
        val currentHandlers = if (parallel) handlers.copy() else handlers
        if (mappingsName.isNotBlank()) {
            // TODO: Make this lazy
            // TODO: Or maybe use Gradle Dependency API for downloading mappings?
            val version = MappingVersions.getFromString(mappingsName)
            val target = version.target
            val url = BONUtils.toUrl(version.url)
            try {
                currentHandlers.startWithoutProgress("Downloading ${target.name}")
                if (!DownloadUtils.downloadWithCache(url, target, false, false, currentHandlers)) {
                    if (target.exists()) target.delete()
                    currentHandlers.handleError("Failed to download: $url to ${target.absolutePath}", false)
                }
            } catch (e: Exception) {
                if (target.exists()) target.delete()
                currentHandlers.handleError("Failed to download: $url to ${target.absolutePath}", false)
                throw GradleScriptException("Failed when remapping file ${inJar.absolutePath}", e)
            }
            val loadedMappings = loadMappings(version.target, currentHandlers)
            remap(loadedMappings, inJar, outJar, currentHandlers, reverse)
        } else if (mappings.isNotEmpty())
            remap(mappings, inJar, outJar, currentHandlers, reverse)
        else throw IllegalArgumentException("No mappings provided")
    }

    private fun remap(
        mappings: Map<String, String>,
        inJar: File,
        outJar: File,
        handlers: Bon2Handlers,
        reverse: Boolean
    ) {
        val remapper = SimpleRemapper(if (reverse) mappings.entries.associateBy({ it.value }) { it.key } else mappings)
        val classes = JarUtils.readFromJar(inJar, handlers, handlers)
        JarUtils.writeToJar(classes, outJar, remapper, handlers)
    }

    private fun loadMappings(mappings: File, handlers: Bon2Handlers): Map<String, String> =
        loadMappings(mappings, "fields.csv", handlers) + loadMappings(mappings, "methods.csv", handlers)

    private fun loadMappings(zip: File, entry: String, handlers: Bon2Handlers): Map<String, String> {
        val lines =
            String(IOUtils.getZipData(zip, entry), StandardCharsets.UTF_8).lines().drop(1).filter { it.isNotBlank() }
        handlers.start(lines.size - 1, "Loading mappings from CSV file $entry in zip $zip")
        val result = mutableMapOf<String, String>()
        var split: List<String>
        for ((index, line) in lines.withIndex()) {
            handlers.setProgress(index)
            split = line.split(",")
            if (split.size > 1) result[split[0]] = split[1]
            else handlers.handleError("Found bad line in $entry of $zip: $line", true)
        }
        return result
    }
}
