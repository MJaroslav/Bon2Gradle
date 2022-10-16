# Bon2Gradle

Plugin for Minecraft Dev projects that allows use BON2 as analog of `fg.reobf` from ForgeGradle.

[![GitHub issues](https://img.shields.io/github/issues/MJaroslav/Bon2Gradle)](https://github.com/MJaroslav/Bon2Gradle/issues "GitHub issues")
[![GitHub forks](https://img.shields.io/github/forks/MJaroslav/Bon2Gradle)](https://github.com/MJaroslav/Bon2Gradle/network "GitHub forks")
[![GitHub stars](https://img.shields.io/github/stars/MJaroslav/Bon2Gradle)](https://github.com/MJaroslav/Bon2Gradle/stargazers "GitHub stars")
[![GitHub license](https://img.shields.io/github/license/MJaroslav/Bon2Gradle)](https://github.com/MJaroslav/Bon2Gradle/blob/master/LICENSE "Open license")
[![JitPack](https://jitpack.io/v/MJaroslav/Bon2Gradle.svg)](https://jitpack.io/#MJaroslav/Bon2Gradle "JitPack")
[![JitCI status](https://jitci.com/gh/MJaroslav/Bon2Gradle/svg)](https://jitci.com/gh/MJaroslav/Bon2Gradle "JitCI")
![GitHub CI test status](https://github.com/MJaroslav/Bon2Gradle/actions/workflows/ci-test.yml/badge.svg)

## Usage

### Dependencies

- Gradle 6.8+ (Provider as dependency notation).
- Java 8+ (lambdas and streams).

### Adding plugin to build script

```groovy
buildscript {
    repositories {
        // Add JitPack.io to your build script repositories
        maven { url 'https://jitpack.io' } 
        
        // ForgeGradle stuff
        maven { 
            name 'forge'
            url 'https://maven.minecraftforge.net/'
        }
    }
    dependencies {
        // ForgeGradle (fork by anatawa12) stuff
        classpath('com.anatawa12.forge:ForgeGradle:1.2-1.0.+') {
            changing = true
        }
        
        // Add this dependency, you can use "master-SNAPSHOT" as version for using last commit
        classpath 'com.github.MJaroslav:Bon2Gradle:0.3.1'
    }
}

apply plugin: 'bon2gradle' // Applying plugin
```

### Plugin configuring

```groovy
// Extension for BON2 configuring
// option = defaultValue // Description
bon2 {
    useParallelDeobfuscation = false // If dependency contains more than one file,
    // then they will deobfuscated paralelly
    forceMapping = false // Use only next mapping for all dependencies
    forcedMappingLocation = null // Directory with methods.csv and fields.csv files
    forcedMappingRelativeConfPath = null // Additional relative path from uppder parameter file.
    // ^ 'unpacked/conf' will be used if not present ^
    
    mappingProvider = 'io.github.mjaroslav.bon2gradle.api.impl.FG12Provider'
    // Just 'io.github.mjaroslav.bon2gradle.api.MappingProvider' interface realization
    // For getting your current Minecraft Dev plugin mapping     
}
```

### Deobfuscating dependencies

Just wrap dependency identifier string to `bon2.deobf` method:

```groovy
repositories {
    mavenCentral()
    maven {
        url "https://cursemaven.com/"
        content {
            includeGroup "curse.maven"
        }
    }
}

dependencies {
    // HEE mod from cursemaven just for example
    // All deobfuscated jars will be saved in special directory insode of project build directory.
    implementation bon2.deobf("curse.maven:hardcore-ender-expansion-228015:2316923")
}

```

## Supported Minecraft Dev Plugins

- It's written and tested with ForgeGradle-1.2 fork by anatawa12.
- In theory, if I don't use any code of FG outside from their MappingProvider then
  you can use it with any other plugin.

## Building

Just clone repository, checkout to this branch and run `./gradlew build`. It will build project with unit tests, if you
want to run functional tests to, run it with `CI=true` environment variable.

Minimal required JDK version is `8`. You can see some syntax sugar from the newest versions, it possible by Jabel
plugin.

## Post Scriptum

Feel free to correct typos and errors in the text or code :)
