# Potassium: OptiDex

A lightweight companion foundation for Minecraft Java Edition 1.8.9 Forge, designed to coexist with OptiFine.

## Target platform

- Minecraft 1.8.9
- Forge 11.15.1.2318
- Java 8
- ForgeGradle 2.1 / Gradle wrapper 2.14.1

## Current implementation

### Read-only client diagnostics

- `/potassium` or `/potassium status` — show Minecraft and Java versions, operating system/architecture, CPU threads, JVM memory, OptiFine detectability, and optimization-module status.
- `/potassium report` — write a local `potassium-diagnostics.txt` report in the Minecraft game directory.

### Bounded background diagnostics I/O

Report disk writes run on one daemon worker instead of the Minecraft client thread. Its one-task queue bounds pending work and rejects excess requests rather than growing without limit. It writes a temporary file and replaces the report atomically where supported; completion messages are scheduled back onto the client thread. This avoids blocking a client tick on report disk I/O. It is not a general FPS optimization.

Reports remain local; no telemetry is uploaded.

## Compatibility boundaries

OptiFine/vanilla retain ownership of rendering, chunk compilation, GPU submission, culling, particles, zoom, and visual settings. Potassium currently installs no renderer or chunk hooks and does not claim measurable FPS improvements.

## Next optimization work

Only add independent optimizations with a real consumer and measurable benefit: profile-driven CPU/entity workload reductions, bounded/cancellable background work, cleanup for state Potassium actually owns, and resource-reload deduplication only when profiling proves it useful. Test with OptiFine present and absent.

No custom settings GUI, permanent HUD, duplicate video controls, developer panel, or pet system is planned.

## Build

Use JDK 8:

```bash
chmod +x gradlew
./gradlew setupDecompWorkspace build --no-daemon
```

The JAR is generated in `build/libs/`. CI success does not replace an in-game test with the exact OptiFine build and modpack. No public release, tag, or downloadable version is created without explicit permission.
