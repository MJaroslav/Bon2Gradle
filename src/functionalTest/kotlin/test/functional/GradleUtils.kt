package io.github.mjaroslav.bon2gradle.test.functional

import org.gradle.testkit.runner.GradleRunner

fun getClasspathString(runner: GradleRunner): String =
    runner.pluginClasspath.joinToString(",") { "'${it.absolutePath}'" }
