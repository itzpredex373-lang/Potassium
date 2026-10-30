# Potassium: OptiDex

A lightweight companion foundation for Minecraft Java Edition 1.8.9 Forge, designed to coexist with OptiFine.

## Target platform

- Minecraft Java Edition 1.8.9
- Minecraft Forge 11.15.1.2318
- Java 8
- ForgeGradle 2.1 / Gradle wrapper 2.14.1

## Current implementation

### Read-only client diagnostics

Run `/potassium` or `/potassium status` in-game to display:
- Minecraft version
- Java runtime version
- JVM memory used, allocated, and maximum
- Whether the OptiFine Config class is detectable
- Current optimization-module status

The command does not change settings, force garbage collection, add a HUD, or modify rendering.

## Compatibility boundaries

OptiFine/vanilla retain ownership of rendering, chunk compilation, GPU submission, culling, particles, zoom, and visual settings. Potassium currently installs no renderer or chunk hooks and does not claim measurable FPS improvements.

## Planned independent features

Features will be added only when they are useful, measurable, safe for Minecraft 1.8.9, and do not duplicate OptiFine:
- Crash-safe environment diagnostic reports
- A bounded API for Potassium-owned background work
- Conservative world-transition state tracking
- Resource-reload work deduplication where profiling proves a benefit
- Compatibility checks with OptiFine both present and absent

No custom settings GUI, permanent HUD, duplicate video controls, developer panel, or pet system is planned.

## Build

This is a legacy ForgeGradle 2.1 project and requires JDK 8.

Windows:
```bat
gradlew.bat setupDecompWorkspace
gradlew.bat build
```

Linux/macOS:
```bash
chmod +x gradlew
./gradlew setupDecompWorkspace
./gradlew build
```

The JAR is generated in `build/libs/`. A successful CI build does not replace an in-game test with the exact OptiFine build and modpack.

No public release, tag, or downloadable version is created without explicit permission.
