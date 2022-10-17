/**
 * Just copypaste from anatawa12 ForgeGradle fork.
 *
 * @author <a href="https://github.com/MinecraftForge">MinecraftForge</a> and <a href="https://github.com/anatawa12">anatawa12</a>
 */
package io.github.mjaroslav.bon2gradle.test.shared

import org.junit.jupiter.api.Assertions
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.MethodNode
import java.io.File
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarFile

fun compareJarClassMembers(expected: File, actual: File) =
    JarFile(expected).use { expectedJarFile ->
        JarFile(actual).use { actualJarFile ->
            compareJarClassMembers(
                expectedJarFile,
                actualJarFile
            )
        }
    }

fun compareJarClassMembers(expected: Path, actual: Path) = compareJarClassMembers(expected.toFile(), actual.toFile())

fun compareJarClassMembers(expected: JarFile, actual: JarFile) {
    var visitedAnyClasses = false
    expected.stream().filter { it.name.endsWith(".class") }.forEach {
        visitedAnyClasses = true
        val expectedNode = readClassNodeFromJarEntryInJarFile(expected, it)

        Assertions.assertNotNull(expectedNode, "Should load expected node")
        val changedNode = readClassNodeFromJarEntryInJarFile(actual, actual.getJarEntry(it.name))
        Assertions.assertNotNull(changedNode, "Should load changed node")

        val expectedFieldsNames = expectedNode!!.fields.map { field -> key(field) }.toSet()
        val changedFieldsNames = changedNode!!.fields.map { field -> key(field) }.toSet()
        Assertions.assertTrue(expectedFieldsNames == changedFieldsNames, "Fields names not equals")

        val expectedMethodsNames = expectedNode.methods.map { method -> key(method) }.toSet()
        val changedMethodsNames = changedNode.methods.map { method -> key(method) }.toSet()
        Assertions.assertTrue(expectedMethodsNames == changedMethodsNames, "Methods names not equals")
    }
    Assertions.assertTrue(visitedAnyClasses, "Should visit classes in expected jar $expected")
}

private fun key(node: MethodNode): String = node.name + ':' + node.desc

private fun key(node: FieldNode): String = node.name + ':' + node.desc

private fun readClassNodeFromJarEntryInJarFile(jarFile: JarFile, jarEntry: JarEntry?): ClassNode? =
    if (jarEntry == null) null else jarFile.getInputStream(jarEntry).use {
        val node = ClassNode()
        val classReader = ClassReader(it)
        classReader.accept(node, 0)
        return node
    }
