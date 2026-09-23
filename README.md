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
- Chunk usefulness/distance checks
- Configurable per-tick chunk-work budget

## Part 1 — Rendering

Potassium can cancel the rendering of living entities that are farther than
the configured entity-render distance.

Defaults:

- `optimizeEntityRendering = true`
- `entityRenderDistance = 96` blocks

The render event hook is now registered only from the client proxy. This keeps
client-only Forge rendering classes out of the dedicated-server initialization
path.

## Part 2 — Chunk optimization

Part 2 adds:

- `ChunkOptimizer` for cheap player-to-chunk distance decisions
- `ChunkUpdateOptimizer` for a bounded per-client-tick work budget
- `ChunkRenderScheduler` as the client scheduler entry point

Defaults:

- `optimizeChunkUpdates = true`
- `chunkUpdateRadius = 12` chunks
- `maxChunkUpdatesPerTick = 2`

This is deliberately a safe foundation. It does **not** unload chunks or
rewrite Minecraft's internal `RenderGlobal`/`RenderChunk` implementation.
A later hook can use the scheduler budget without creating an unbounded amount
of chunk work in one frame.

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
    └── chunks/
        ├── ChunkOptimizer.java
        ├── ChunkUpdateOptimizer.java
        └── ChunkRenderScheduler.java
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
