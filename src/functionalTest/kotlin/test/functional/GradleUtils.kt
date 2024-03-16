package io.github.mjaroslav.bon2gradle.test.functional

import org.gradle.testkit.runner.GradleRunner

class GradleUtils {
    companion object {
        @JvmStatic
        fun getClasspathString(runner: GradleRunner): String =
            runner.pluginClasspath.joinToString(",") { "'${it.absolutePath}'" }
    }
}

