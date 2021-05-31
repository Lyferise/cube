# Cube

This is a Git [monorepo](https://en.wikipedia.org/wiki/Monorepo) for Cube, structured as a Gradle/IntelliJ multi-module project. Each module builds as a separate JAR, but are included in a [multi-module project](https://docs.gradle.org/current/userguide/multi_project_builds.html) for convenience, as well as to aid source code navigation and local debugging.

## Modules

- `cube-cli`
- `cube-client`
- `cube-cluster`
- `cube-core`
- `cube-lang`

## Prerequisites

- [JDK 14.0.2](https://www.oracle.com/java/technologies/javase/jdk14-archive-downloads.html)
- [Gradle 7.0](https://gradle.org/releases)