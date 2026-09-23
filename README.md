# Potassium

Potassium is a Minecraft Java Edition **1.8.9 Forge** performance mod focused on
low-end hardware and Java launchers.

## Current implementation

- Forge 1.8.9 mod bootstrap
- Persistent Forge configuration
- Common/client sided proxy separation
- Central performance manager and client tick scheduler
- Part 1: distance-based living-entity render culling
- Part 2: bounded client chunk-work scheduler foundation
- Part 3: entity update throttling and client particle budgeting
- Part 4: adaptive JVM memory-pressure and client tick-time monitoring

## Part 1 — Rendering

Potassium can cancel the rendering of living entities that are farther than
the configured entity-render distance.

Defaults:

- `optimizeEntityRendering = true`
- `entityRenderDistance = 96` blocks

The render event hook is registered only from the client proxy.

## Part 2 — Chunk optimization

Part 2 adds:

- `ChunkOptimizer` for cheap player-to-chunk distance decisions
- `ChunkUpdateOptimizer` for a bounded per-client-tick work budget
- `ChunkRenderScheduler` as the client scheduler entry point

Defaults:

- `optimizeChunkUpdates = true`
- `chunkUpdateRadius = 12` chunks
- `maxChunkUpdatesPerTick = 2`

This is deliberately a safe foundation. It does **not** unload chunks or rewrite
Minecraft's internal `RenderGlobal`/`RenderChunk` implementation.

## Part 3 — Entity and particle optimization

### Entities

Potassium includes an experimental distant-living-entity update throttle.

Defaults:

- `reduceEntityUpdates = false`
- `entityUpdateDistance = 64` blocks

It is disabled by default because cancelling `LivingUpdateEvent` can affect
AI, movement, and gameplay behavior.

### Particles

Potassium reads Minecraft 1.8.9's client `EffectRenderer` particle layers and
keeps the total particle list bounded by the configured budget.

Defaults:

- `reduceParticles = true`
- `maxParticlesPerTick = 80`

The particle cleanup is CPU-aware and can back off when the client is already
spending too much time in its tick.

## Part 4 — Memory and CPU optimization

Part 4 adds lightweight adaptive monitoring rather than forcing garbage
collection or creating extra worker threads.

### Memory

`MemoryOptimizer` monitors JVM heap pressure using `Runtime`. When adaptive
performance is enabled and heap usage becomes high, Potassium reduces the
optional particle budget.

Defaults:

- `adaptivePerformance = true`
- `memoryPressureThreshold = 85` percent

At very high heap pressure, the optional particle budget can be reduced further.

### CPU / client tick time

`CpuOptimizer` measures client tick duration with `System.nanoTime()` and
uses an allocation-free moving average. Optional particle maintenance is skipped
when the moving average exceeds the configured budget.

Default:

- `cpuBudgetMillis = 45` ms

This does not claim to reduce all Minecraft CPU work; it prevents Potassium's
optional maintenance from adding work when the client is already overloaded.

## Project structure

~~~text
src/main/java/com/predex/potassium/
├── Potassium.java
├── PotassiumEventHandler.java
├── config/
│   └── PotassiumConfig.java
├── proxy/
│   ├── PotassiumProxy.java
│   └── PotassiumClientProxy.java
└── optimization/
    ├── PerformanceManager.java
    ├── rendering/
    │   ├── RenderOptimizer.java
    │   ├── BlockRenderOptimizer.java
    │   └── EntityRenderOptimizer.java
    ├── chunks/
    │   ├── ChunkOptimizer.java
    │   ├── ChunkUpdateOptimizer.java
    │   └── ChunkRenderScheduler.java
    ├── entities/
    │   ├── EntityOptimizer.java
    │   └── EntityUpdateHandler.java
    ├── particles/
    │   ├── ParticleOptimizer.java
    │   └── ParticleClientScheduler.java
    └── system/
        ├── MemoryOptimizer.java
        ├── CpuOptimizer.java
        └── SystemPerformanceScheduler.java
~~~

## Build

This is an old ForgeGradle 2.1 project and is intended to be built with
**JDK 8**.

~~~bash
gradle setupDecompWorkspace
gradle build
~~~

The compiled mod JAR is produced under:

~~~text
build/libs/
~~~

## Important

Potassium does not claim a fixed FPS increase. Actual gains depend on the
Minecraft scene, entity count, render distance, GPU/CPU, drivers, launcher,
resource pack, and other installed mods.

Optimization modules are added incrementally and should be benchmarked before
being treated as production-ready.
