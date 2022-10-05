package com.github.mjaroslav.bon2gradle.util;

import lombok.experimental.UtilityClass;
import lombok.val;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Just copypaste from anatawa12 ForgeGradle fork.
 *
 * @author <a href="https://github.com/MinecraftForge">MinecraftForge</a> and <a href="https://github.com/anatawa12">anatawa12</a>
 */
@UtilityClass
public class JarComparison {
    public void compareJarClassMembers(File expected, File actual) throws IOException {
        try (JarFile expectedJarFile = new JarFile(expected);
             JarFile actualJarFile = new JarFile(actual)) {
            compareJarClassMembers(expectedJarFile, actualJarFile);
        }
    }

    public void compareJarClassMembers(@NotNull JarFile expected, @NotNull JarFile actual) throws IOException {
        boolean visitedAnyClasses = false;
        for (var entry : (Iterable<JarEntry>) expected.stream()::iterator) {
            if (!entry.getName().endsWith(".class"))
                continue;

            visitedAnyClasses = true;
            val expectedNode = readClassNodeFromJarEntryInJarFile(expected, entry);

            Assertions.assertNotNull(expectedNode, "Should load expected node");
            val changedNode = readClassNodeFromJarEntryInJarFile(actual, actual.getJarEntry(entry.getName()));
            Assertions.assertNotNull(changedNode, "Should load changed node");

            for (var outer : expectedNode.fields) {
                var found = false;
                for (var inner : changedNode.fields)
                    if (key(inner).equals(key(outer))) {
                        found = true;
                        break;
                    }
                Assertions.assertTrue(found, "Should find field " + key(outer) + " in " + changedNode.name);
            }

            for (var outer : expectedNode.methods) {
                var found = false;
                for (var inner : changedNode.methods)
                    if (key(inner).equals(key(outer))) {
                        found = true;
                        break;
                    }
                Assertions.assertTrue(found, "Should find method " + key(outer) + " in " + changedNode.name +
                    ", actual methods\n" + changedNode.methods.stream().map(JarComparison::key).collect(Collectors.toList()));
            }
        }

        Assertions.assertTrue(visitedAnyClasses, "Should visit classes in expected jar " + expected);
    }

    @Contract(pure = true)
    private @NotNull String key(@NotNull MethodNode node) {
        return node.name + ':' + node.desc;
    }

    @Contract(pure = true)
    private @NotNull String key(@NotNull FieldNode node) {
        return node.name + ':' + node.desc;
    }

    private ClassNode readClassNodeFromJarEntryInJarFile(JarFile jarFile, JarEntry jarEntry) throws IOException {
        if (jarEntry == null)
            return null;
        try (val is = jarFile.getInputStream(jarEntry)) {
            val node = new ClassNode();
            val classReader = new ClassReader(is);
            classReader.accept(node, 0);
            return node;
        }
    }
}
