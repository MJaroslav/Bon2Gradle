# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.3.0] - 2022-10-14

### Fixed

- Sometimes deobfuscated jars not appear in their dir. Task `clean` just removes it. Now, deobfuscated jars directory
  moved to caches in gradle home.

### Added

- Parallel dependency deobfuscation.
    - With toggle option in `bon2` extension.

### Edited

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

[unreleased]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.3.0...HEAD

[0.3.0]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.2.0...v0.3.0

[0.2.0]: https://github.com/MJaroslav/Bon2Gradle/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/MJaroslav/Bon2Gradle/releases/tag/v0.1.0
