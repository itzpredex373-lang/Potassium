# Potassium: OptiDex

A lightweight companion foundation for Minecraft Java Edition 1.8.9 Forge, designed to coexist with OptiFine.

## Target platform

- Minecraft 1.8.9
- Forge 11.15.1.2318
- Java 8
- ForgeGradle 2.1 / Gradle wrapper 2.14.1

## Current implementation

### Read-only client diagnostics

Run these commands in-game:
- `/potassium` or `/potassium status` — show Minecraft and Java versions, JVM memory, OptiFine detectability, and optimization-module status.
- `/potassium report` — write a local `potassium-diagnostics.txt` report in the Minecraft game directory.

The report contains basic runtime, operating-system, processor, memory, and OptiFine-detection information. It is saved locally; Potassium does not upload telemetry. The report may be overwritten by a later report command.

Commands do not change settings, force garbage collection, add a HUD, or modify rendering.

## Compatibility boundaries

OptiFine/vanilla retain ownership of rendering, chunk compilation, GPU submission, culling, particles, zoom, and visual settings. Potassium currently installs no renderer or chunk hooks and does not claim measurable FPS improvements.

## Planned independent features

Features will be added only when useful, measurable, safe for Minecraft 1.8.9, and not duplicating OptiFine:
- A bounded API for Potassium-owned background work, once a real consumer exists
- Conservative world-transition state tracking, if needed by an implemented feature
- Resource-reload work deduplication only if profiling proves a benefit
- Compatibility checks with OptiFine both present and absent

No custom settings GUI, permanent HUD, duplicate video controls, developer panel, or pet system is planned.

## Build

Use JDK 8:

```bash
chmod +x gradlew
./gradlew setupDecompWorkspace build --no-daemon
```

The JAR is generated in `build/libs/`. A successful CI build does not replace an in-game test with the exact OptiFine build and modpack.

No public release, tag, or downloadable version is created without explicit permission.
