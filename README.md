# Potassium

Potassium is a Minecraft Java Edition **1.8.9 Forge** performance mod focused on
low-end hardware and Java launchers.

## Current implementation

The current codebase contains:

- Forge 1.8.9 mod bootstrap
- Persistent Forge configuration
- Central performance manager and client tick scheduler
- Rendering optimization module
- Distance-based living-entity render culling
- Chunk-distance helper for the upcoming chunk optimization module
- Conservative defaults intended for low-end systems

## Rendering optimization

The first real optimization pass is intentionally conservative. Potassium can
cancel the rendering of living entities that are farther than the configured
entity-render distance.

Default settings:

- optimizeEntityRendering = true
- entityRenderDistance = 96 blocks

The optimization can be disabled from the generated Forge configuration file.

## Project structure

~~~text
src/main/java/com/predex/potassium/
├── Potassium.java
├── PotassiumEventHandler.java
├── config/
│   └── PotassiumConfig.java
└── optimization/
    ├── PerformanceManager.java
    └── rendering/
        ├── RenderOptimizer.java
        ├── BlockRenderOptimizer.java
        └── EntityRenderOptimizer.java
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

New optimization modules should be added incrementally and benchmarked before
being treated as production-ready.
