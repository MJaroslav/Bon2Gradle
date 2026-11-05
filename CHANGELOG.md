# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.6.0] - 2026-11-05

### Added

- `deobf` functions supports Dependency Providers now.

## [0.5.0] - 2024-03-18

### Added

- `deobf` function variant with Closure for "pure" dependency.
- `deobf` function with Dependency object as argument.
- Clean tasks:
  - `cleanDeobfCache` - remove all deobfuscated artifacts cache (just run next two tasks).
  - `cleanLocalDeobfCache` - remove local (in project build directory) deobfuscated artifacts cache.
  - `cleanGlobalDeobfCache` - remove global (in Gradle home caches) deobfuscated artifacts cache.
- Deobfuscated dependencies now have version
  suffix: `_deobfuscated_with_{MAPPINGS_CHANNEL}_{MAPPINGS_VERSION}-{MC_VERSION}` for "pure" artifacts
  and `_deobfuscated_with_DIRTY` for "dirty" artifacts.
- Now deobfuscated artifacts location contains subdirectories with group, name and version like as in maven.
- [Bon2Task](src/main/kotlin/tasks/Bon2Task.kt) now have "reverse" option (maybe buggy).
- All deobfuscation performs after project evaluation or on resolve request (extension option).
- Extension option for forcing saving all deobfuscated artifacts locally in project (mark all as "dirty").

### Fixed

- Issue [#1](https://github.com/MJaroslav/Bon2Gradle/issues/1) (ignoring :: references on remapping) by
  using [GTNewHorizons/BON2](https://github.com/GTNewHorizons/BON2)
  fork.

### Changed

- Support Gradle 8.5 (tested).
- `deobf` function now create `Dependency` object instead of `Provider<Dependency>`.
- [MappingProvider](src/main/kotlin/api/MappingProvider.kt):
  - Some helper functions: CSV file to `Map<String, String>` parser and task executor.
  - Now contains two functions: one for "pure" mapping name and second for "dirty" mapping `Map<String, String>`.
    "pure" has a higher priority than "dirty" function.
- `force` options in extension changed like as MappingProvider.
- Maximum abstraction from Internal classes (but not fully).
- Some additions in tests.

### Removed

- Internal implementation of `Provider<Dependency>`.

## [0.4.0] - 2022-10-17

### Changed

- Full switch to using kotlin as build script and source language.

## [0.3.1] - 2022-10-16

### Fixed

- Project configuration error with "Plugin class is abstract" error on some versions of Gradle.

## [0.3.0] - 2022-10-14

### Fixed

- Sometimes deobfuscated jars not appear in their dir. Task `clean` just removes it. Now, deobfuscated jars directory
  moved to caches in gradle home.

### Added

- Parallel dependency deobfuscation.
  - With toggle option in `bon2` extension.

### Changed

- Package structure refactor.
- Using GradleException based exceptions in "dependency resolving/deobfuscation code".

## [0.2.0] - 2022-10-10

### Fixed

- BON resolving problem by using my own fork of this library.

## [0.1.0] - 2022-10-09

### Added

- You can wrap your dependency in `bon2.deobf` method for deobfuscate it.
- Gradle extension with configuration.
  - Force mappings.
  - Mappings provider (if you use another MC dev plugin).
- Bon2Task for use BON2 in Gradle.

[unreleased]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.5.0...HEAD

[0.6.0]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.5.0...v0.6.0

[0.5.0]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.4.0...v0.5.0

[0.4.0]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.3.1...v0.4.0

[0.3.1]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.2.0...v0.3.0

[0.2.0]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/MJaroslav/Bon2Gradle/releases/tag/v0.1.0
