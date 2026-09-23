# Potassium

Potassium is a Minecraft Java Edition **1.8.9 Forge** performance mod focused on
low-end hardware and Java launchers.

## Roadmap / implementation status

Potassium is being developed as an optimization-first project. **Part 6 (Settings
UI) is intentionally skipped for now.** The engine work in Parts 1-5 and the
advanced Parts 7-11 is prioritized first.

### Part 1 — Rendering Engine
- Living-entity distance culling
- Frustum culling
- Conservative occlusion/visibility gate
- Block/chunk distance helpers
- Render-pass/state helper and state cache
- Vertex-buffer and texture-binding helper layers
- Adaptive render distance scaling

Deep RenderGlobal/RenderChunk replacement and true GPU occlusion queries are
not enabled yet because those require careful 1.8.9 renderer/coremod integration.

### Part 2 — Chunk Engine
- Chunk build scheduling foundation
- Nearest-first priority queue
- Player-proximity priority
- Bounded per-tick chunk budget
- Rebuild deduplication
- World-update gating
- Optional async preparation API for pure data work

The async API deliberately does not move Minecraft world/render objects off the
client thread. Vanilla RenderGlobal/RenderChunk rebuilding is not rewritten yet.

### Part 3 — Entity & Particle
- Entity render distance/culling
- Experimental distant living-entity update throttling
- Particle budget and adaptive trimming
- Particle culling helper
- Bounded particle object-pool helper

Gameplay-affecting entity update throttling remains disabled by default.

### Part 4 — CPU & Memory
- Client tick CPU budget
- JVM heap pressure monitoring
- Allocation/object-reuse helpers
- Mesh-data reuse pool
- Bounded cache management
- Adaptive scheduler
- Stability guard / fail-safe degradation

Potassium does not force garbage collection.

### Part 5 — Low-End Engine
Profiles:
- BALANCED
- LOW_END
- ULTRA_LOW

Also included:
- Dynamic quality scaling
- CPU/memory-aware workload scaling
- JVM processor/heap based hardware tier detection
- Hardware work scaling

### Part 6 — Settings UI
**Skipped for now.** No settings UI work is being prioritized until the engine
parts are substantially complete.

### Part 7 — Advanced Rendering
Engine foundations added for:
- Vertex buffer optimization
- Render-state caching
- Texture-binding reduction
- Display-list/render-pass helper compatibility
- Draw-call/state tracking foundations

Actual vanilla renderer bytecode replacement is intentionally deferred until
1.8.9 runtime mappings are tested.

### Part 8 — Memory Engine
Added:
- Chunk memory estimation helpers
- Mesh/data reuse pools
- LRU cache management
- Allocation tracking

### Part 9 — World Optimization
Added foundations for:
- Tile-entity validity checks
- Light-update scheduling
- Optional world-update budgeting
- Redundant update deduplication

Vanilla world ticks are not globally cancelled.

### Part 10 — Compatibility
Added:
- Forge presence/fallback checks
- OptiFine detection
- Mod-loaded checks
- Safe hook gating

When OptiFine is detected, Potassium's event-based living-entity render hook
fails open until a dedicated compatibility layer is implemented.

### Part 11 — Benchmark & Stability
Added:
- Frame-time measurement
- Client tick-time measurement
- Chunk-work timing instrumentation
- JVM memory measurement
- Measured FPS counter
- Allocation counters
- Stability degradation guard

These metrics are instrumentation only; Potassium does not promise a fixed FPS
gain without real hardware and scene benchmarks.

## Build

This is a legacy **ForgeGradle 2.1** project for Minecraft 1.8.9 and is intended
to be built with **JDK 8**. Use a legacy Gradle release compatible with
ForgeGradle 2.1; Gradle 3.x/4.x are commonly used with this toolchain. Do not
use a current Gradle 8.x release with this build.

The project currently does not include a Gradle wrapper, so Gradle must be
installed on the build machine.

    gradle setupDecompWorkspace
    gradle build

If dependency resolution fails because of stale legacy caches, try:

    gradle clean
    gradle setupDecompWorkspace --refresh-dependencies
    gradle build

The compiled mod JAR is produced under:

    build/libs/

## Runtime test

Use a clean Minecraft **1.8.9 Forge 11.15.1.2318** profile for the first test.
Forge lists 11.15.1.2318 as the recommended 1.8.9 build.

For compatibility testing, first test Potassium alone. Then test it with other
mods one group at a time. Gameplay-affecting entity-update throttling is off by
default.

## Important

Potassium does not claim a fixed FPS increase. Actual gains depend on the
Minecraft scene, entity count, render distance, GPU/CPU, drivers, launcher,
resource pack, and other installed mods.

The current advanced modules are intentionally conservative foundations. Actual
1.8.9 renderer/chunk internals must be runtime-tested before enabling invasive
bytecode/coremod hooks.
