# Bon2Gradle

Plugin for Minecraft Dev projects that allows use BON2 as analog of `fg.deobf` from ForgeGradle.

[![GitHub issues](https://img.shields.io/github/issues/MJaroslav/Bon2Gradle)](https://github.com/MJaroslav/Bon2Gradle/issues "GitHub issues")
[![GitHub forks](https://img.shields.io/github/forks/MJaroslav/Bon2Gradle)](https://github.com/MJaroslav/Bon2Gradle/network "GitHub forks")
[![GitHub stars](https://img.shields.io/github/stars/MJaroslav/Bon2Gradle)](https://github.com/MJaroslav/Bon2Gradle/stargazers "GitHub stars")
[![GitHub license](https://img.shields.io/github/license/MJaroslav/Bon2Gradle)](https://github.com/MJaroslav/Bon2Gradle/blob/master/LICENSE "Open license")
[![JitPack](https://jitpack.io/v/MJaroslav/Bon2Gradle.svg)](https://jitpack.io/#MJaroslav/Bon2Gradle "JitPack")
[![JitCI status](https://jitci.com/gh/MJaroslav/Bon2Gradle/svg)](https://jitci.com/gh/MJaroslav/Bon2Gradle "JitCI")
![GitHub CI test status](https://github.com/MJaroslav/Bon2Gradle/actions/workflows/ci-test.yml/badge.svg)

## Usage

### Dependencies

- Gradle 6.8+ (minimum tested version).
  - Tests configured for next versions: `6.8`, `6.9.4`, `7.5.1`, `7.6.4`, `8.5`.
- Kotlin + Java 8+ (lambdas and streams).

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
        classpath('com.anatawa12.forge:ForgeGradle:1.2-1.1.+') {
            changing = true
        }
        
        // Add this dependency, you can use "master-SNAPSHOT" as version for using last commit
        classpath 'com.github.MJaroslav:Bon2Gradle:0.5.0'
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
    
    force = false // Use mappings provided by "mappingsName" or by "csv"
    
    mappingsName = "" // "pure" mappings name in "{mappings_channel}_{mappings_version}-{mc_version}" format
    // "pure" has a higher priority than "dirty" function. Example: stable_12-1.7.10
    
    csv = files() // List of CSV like files of "dirty" mappings
    
    dirty = false // Force saving deobfuscated mappings in project build directory as "dirty"
    
    resolve = true // Automatically deobfuscate all created by bon2.deobf function
    // artifacts after project evaluation
    
    providerClass = 'io.github.mjaroslav.bon2gradle.api.impl.FG12Provider'
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

You can also use Closure as second function argument for configure "pure" dependency.

## Supported Minecraft Dev Plugins

- It's written and tested with ForgeGradle-1.2 fork by anatawa12.
- In theory, if I don't use any code of FG outside from their MappingProvider then
  you can use it with any other plugin.

## Building

Just clone repository, checkout to this branch and run `./gradlew build`. It will build project with unit tests, if you
want to run functional tests to, run it with `CI=true` environment variable or manually run `functionalTest` task.

Minimal required JDK version is `8`.

## Post Scriptum

Feel free to correct typos and errors in the text or code :)
