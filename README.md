# Potassium

Potassium is a Minecraft Java Edition **1.8.9 Forge** performance mod focused on improving frame-time stability, rendering, chunk work, entity/particle processing, CPU/memory usage, and low-end client performance.

The current repository contains both the optimization engine and the in-game Potassium settings UI.

## Current implementation

### Target platform

- Minecraft Java Edition **1.8.9**
- Minecraft Forge **11.15.1.2318**
- Java **8**
- ForgeGradle **2.1**
- Gradle wrapper **2.14.1**

Potassium is designed as a client-side performance mod. It uses Forge event hooks and a core transformer for supported renderer hooks.

---

## Part 1 — Rendering Engine

Implemented rendering/performance systems include:

- Entity render-distance optimization
- Entity occlusion culling
- Frustum visibility optimization
- Conservative occlusion/visibility gates
- Block/face culling helpers
- Render visibility helpers
- Render-pass/state caching
- Render-region scheduling helpers
- Texture-binding optimization
- Vertex-buffer optimization
- Display-list/render-pass compatibility helpers
- Animation visibility optimization
- Fast-render paths
- Fast math helpers
- Empty draw-call skipping
- Render section tracking
- Custom GPU mesh storage
- Custom draw submission with fallback behavior
- Bounded mesh upload pipeline
- CPU-side mesh preparation
- Per-frame mesh upload budgeting
- Per-frame entity occlusion-test budgeting

Potassium also contains Forge 1.8.9 renderer/core hooks through its core plugin and transformer.

The renderer uses conservative fallback behavior where a Potassium path is not safe or complete.

---

## Part 2 — Chunk Engine

Implemented chunk systems include:

- Chunk build scheduling
- Chunk rebuild queue
- Nearest/player-proximity prioritization
- Directional movement-aware prioritization
- Bounded per-tick chunk budgets
- Chunk rebuild deduplication
- Chunk work queues
- Chunk render pipeline
- Chunk async preparation API
- Mobile-oriented chunk streaming
- Movement prediction
- Chunk mesh build queue
- Chunk mesh cache
- Compiled chunk representation
- GPU mesh upload queue
- GPU region management
- Render-section management
- Mesh upload admission control
- Optional lazy chunk preparation
- Optional dynamic chunk updates

Async preparation is designed for safe data preparation. Minecraft world/render objects are not blindly moved to background threads.

---

## Part 3 — Entity & Particle Engine

### Entities

- Entity render optimization
- Entity render-distance control
- Entity occlusion culling
- Optional living-entity update throttling
- Entity update-distance control
- Bounded occlusion-test workload

Gameplay-affecting entity update throttling is **disabled by default**.

### Particles

- Particle processing budget
- Adaptive particle trimming
- Particle culling
- Client-side particle scheduling
- Particle object pooling/reuse

---

## Part 4 — CPU & Memory

Implemented CPU/memory systems include:

- Client CPU workload budgeting
- Adaptive performance controller
- CPU-aware scheduling
- JVM heap-pressure monitoring
- Memory-aware workload scaling
- Allocation optimization helpers
- Allocation tracking
- Object reuse pools
- Mesh-data reuse
- Chunk memory estimation
- Cache management
- Stability/fail-safe degradation
- System performance scheduling

Potassium does **not** force garbage collection.

---

## Part 5 — Low-End / Adaptive Engine

Potassium includes hardware-aware and adaptive systems:

- Hardware profile detection
- CPU/processor-based workload scaling
- JVM heap-based workload scaling
- Dynamic quality control
- Adaptive performance control
- Memory-pressure response
- CPU-budget response
- Low-memory mode
- Mobile-first chunk streaming
- Bounded workload admission

### Performance Profiles

The current implementation has **four selectable profiles**:

| Profile | Purpose |
|---|---|
| **HIGH** | Strong optimization while keeping a more general-purpose balance |
| **MEDIUM / Mid** | Balanced optimization for general use |
| **LOW** | Lighter optimization with more QoL features |
| **PERFORMANCE** | Most aggressive performance-oriented profile, prioritizing raw FPS and frame-time stability |

Profile values automatically configure several performance budgets such as entity distance, particle budget, chunk radius, chunk updates, memory threshold, CPU budget, mesh uploads, and occlusion-test limits.

Profile-specific behavior is also applied to rendering/chunk settings.

---

## Part 6 — Settings UI

**Implemented.**

Potassium currently has a **4-page in-game Settings UI**.

### Main settings

- Master Optimization ON/OFF
- High profile
- Mid profile
- Low profile
- Performance profile
- Fast Render
- Fast Math
- Smart Animations
- Block Face Culling
- Entity Culling
- Render Regions
- Chunk Optimization
- Lazy Chunk Loading
- Dynamic Chunk Updates
- Adaptive Performance

### Advanced performance settings

- Entity Rendering
- Entity Update Optimization
- Particle Optimization
- Low Memory Mode
- Entity Render Distance
- Entity Update Distance
- Particle Budget
- Chunk Radius
- Chunk Budget
- CPU Budget
- Memory Pressure Threshold
- Renderer Hooks
- Empty Draw Skipping
- Smooth World
- Chunk Streaming
- Movement Prediction
- FPS Smoothing
- Render Sections
- Mesh Upload Budget
- Mesh Preparation
- Occlusion-Test Budget
- Mesh Upload Pipeline

### Video settings

Potassium also provides a dedicated video-settings screen with Minecraft-style video options, including graphics/render-distance related controls and other client visual settings.

### Mouse-hover descriptions

Settings buttons provide mouse-hover descriptions explaining what each option changes.

---

## Quality-of-Life HUD

Potassium includes a lightweight in-game QoL HUD.

Available information includes:

- FPS
- 1% low FPS
- 0.1% low FPS
- Frame time
- Coordinates
- Facing direction
- Biome
- Java memory usage
- Session time
- Adjustable HUD scale

FPS and low-FPS monitoring remain available independently of the master optimization switch.

This means the user can turn Potassium optimization OFF while keeping performance monitoring available for comparison.

---

## Client-only Mini Pet

Potassium includes a **client-only cosmetic mini-pet system**.

The pet does not create a server-side gameplay entity and does not modify server/world state.

Current implementation provides **45 selectable cosmetic pet types**, including:

- Predex Pet
- Mini King Dragon
- Mini Devil
- Black Dragon
- Wyvern
- Shadow Dragon
- Robot
- Fox
- Kitsune
- Cat
- Heart Cat
- Wolf
- Dog
- Bunny
- Dino
- Bee
- Butterfly
- Crow
- Capybara
- Stag
- Spirit
- Inferno
- Ender
- Slime
- Mini-Me
- Guardian
- Ghost
- Astronaut
- Voidling
- Moon Rabbit
- Mini Phoenix
- Dragon Egg
- Shadow Fox
- Cyber Cat
- Mini Golem
- Ghost Bunny
- Tiny Knight
- Slime King
- Void Orb
- Mini Astral Wolf
- Pixel Robot
- Mini Ender Dragon
- Crystal Fairy
- Tiny Reaper
- Moon Cat

The pet system includes:

- Pet selector
- Pet scale control
- Pet menu
- Pet key handler
- Local name-tag support
- Client-side rendering/animation support

The current profile rules allow the mini pet in the **LOW** profile only. QoL features are available in **MEDIUM/Mid and LOW**; HIGH and PERFORMANCE keep the extra QoL/pet layer restricted so they can prioritize performance.

---

## World Optimization

Implemented world-related systems include:

- Tile-entity validity checks
- Light-update scheduling
- Redundant update tracking
- Optional world-update budgeting
- World-update scheduling
- Smooth world workload distribution
- World transition optimization

Potassium does not globally cancel vanilla world ticks.

---

## Compatibility

Potassium includes compatibility/fallback systems for:

- Forge presence
- Mod-loaded checks
- OptiFine detection
- Safe hook gating
- Renderer fallback paths

OptiFine compatibility is handled conservatively where Potassium's event-based rendering hooks could conflict.

Potassium is intended to be tested first by itself, followed by controlled compatibility testing with other mods.

---

## Benchmark & Telemetry

Potassium includes performance measurement systems for:

- FPS/frame counting
- Frame-time measurement
- 1% low FPS
- 0.1% low FPS
- Client tick-time measurement
- Chunk-work timing
- JVM memory measurement
- Allocation counters
- Performance telemetry
- Benchmark monitoring
- Stability monitoring

These systems measure actual runtime behavior rather than promising a fixed FPS increase.

---

## Master Optimization Switch

The master Potassium optimization switch controls optimization processing.

When optimization is **ON**:

- Potassium optimization workloads are active according to the selected profile/settings.

When optimization is **OFF**:

- Potassium optimization workloads are disabled.
- FPS/frame-time/telemetry monitoring can remain active.
- QoL monitoring can therefore be used to compare the client with and without optimization.

---

## Build

This is a legacy **ForgeGradle 2.1** project for Minecraft 1.8.9 and targets **JDK 8**.

The repository uses a Gradle wrapper/bootstrap setup with **Gradle 2.14.1**.

### Windows

Open Command Prompt in the Potassium folder:

    gradlew.bat setupDecompWorkspace
    gradlew.bat build

After the workspace has been prepared:

    gradlew.bat build

### Linux / macOS

Open a terminal in the Potassium folder:

    chmod +x gradlew
    ./gradlew setupDecompWorkspace
    ./gradlew build

The compiled mod JAR is produced under:

    build/libs/

### CI build

The repository also contains a GitHub Actions build workflow using:

- Ubuntu 22.04
- JDK 8
- Gradle wrapper
- setupDecompWorkspace
- Gradle build
- JAR artifact upload

---

## Runtime testing

For the first test, use a clean Minecraft **1.8.9 Forge 11.15.1.2318** profile.

Recommended test order:

1. Potassium alone
2. Verify the Settings UI
3. Test each performance profile
4. Compare optimization ON/OFF using the QoL HUD
5. Test chunk-heavy scenes
6. Test entity/particle-heavy scenes
7. Test OptiFine compatibility separately
8. Test other mods one group at a time

Performance results depend on the actual scene and hardware.

---

## Important limitations

Potassium does **not** promise a fixed FPS increase.

Actual performance depends on:

- CPU
- GPU
- RAM/JVM heap
- Drivers
- Minecraft settings
- Render distance
- Entity count
- Particle count
- World/chunk complexity
- Resource packs
- Other installed mods
- OptiFine or other renderer modifications
- Launcher/runtime environment

Some advanced renderer and chunk systems use conservative fallbacks because Minecraft 1.8.9's renderer is sensitive to invasive changes.

Runtime benchmarking on real hardware is required before claiming a specific FPS improvement.

---

## Project status

The repository currently contains:

- Rendering optimization
- Chunk optimization
- Entity optimization
- Particle optimization
- CPU/memory optimization
- Adaptive performance systems
- Low-end hardware scaling
- Performance profiles
- Settings UI
- Video settings UI
- QoL HUD
- Client-only mini pets
- World optimization foundations
- Compatibility/fallback systems
- Benchmark and telemetry systems
- Forge core hooks/transformer

The README describes the **current repository implementation**, not the original roadmap state.
