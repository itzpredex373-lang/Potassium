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

### Client particle burst limiter

A client-only event handler admits at most 256 newly-added `EntityFX` particles per client tick and cancels excess additions during unusually large bursts. It does not remove existing particles, change entity ticking, alter server state, or replace OptiFine rendering. The cap is intentionally conservative and needs in-game visual/performance testing; no FPS gain is claimed.

### Bounded background diagnostics I/O

Report disk writes run on one daemon worker instead of the Minecraft client thread. A single-flight guard prevents duplicate reports from building up; the executor also has a bounded queue. The writer uses a temporary file and replaces the report atomically where supported. Completion messages are scheduled back onto the client thread, and worker failures do not leave the writer permanently marked busy. This avoids blocking a client tick on report disk I/O. It is not a general FPS optimization.

Reports remain local; no telemetry is uploaded.

## Optimization status

The requested optimization areas are tracked separately; they are not all implemented yet.

| Area | Current state |
|---|---|
| Stability | Bounded diagnostics worker, duplicate-submit protection, failure cleanup |
| World & server | No world/server optimization installed yet |
| Entity | No entity update or rendering optimization installed yet |
| CPU | Diagnostic file I/O moved off the client thread; no general tick optimization yet |
| Memory | Bounded task admission; no general memory/cache optimizer installed yet |

## Compatibility boundaries

OptiFine/vanilla retain ownership of rendering, chunk compilation, GPU submission, culling, particles, zoom, and visual settings. Potassium currently installs no renderer or chunk hooks and does not claim measurable FPS improvements.

Future work must have a real consumer and measurable benefit: lifecycle cleanup for state Potassium actually owns, cancellable/deduplicated background work, and profile-driven CPU/entity workload reductions. Do not add duplicate OptiFine behavior or empty optimization placeholders.

No custom settings GUI, permanent HUD, duplicate video controls, developer panel, or pet system is planned.

## Build

Use JDK 8:

```bash
chmod +x gradlew
./gradlew setupDecompWorkspace build --no-daemon
```

The JAR is generated in `build/libs/`. CI success does not replace an in-game test with the exact OptiFine build and modpack. No public release, tag, or downloadable version is created without explicit permission.
