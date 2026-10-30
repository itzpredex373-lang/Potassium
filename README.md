# Potassium: OptiDex

**OptiDex** is the OptiFine-compatible performance edition of the Potassium project for Minecraft Java Edition 1.8.9 Forge.

The goal is to let players keep OptiFine installed while using Potassium-owned optimization systems to reduce avoidable CPU/render workload and improve frame-time consistency. Actual FPS gains depend on hardware, world, server, OptiFine settings, and other mods; no fixed FPS increase is guaranteed.

## Target platform

- Minecraft Java Edition 1.8.9
- Minecraft Forge 11.15.1.2318
- Java 8
- ForgeGradle 2.1 / Gradle wrapper 2.14.1

## OptiFine compatibility

OptiDex detects the OptiFine Forge tweaker at runtime.

When OptiFine is detected:
- The core transformer leaves Minecraft renderer classes untouched. It does not inject Potassium hooks into EffectRenderer, Tessellator, RenderGlobal, VboRenderList, ChunkRenderDispatcher, RenderChunk, RenderManager, or BlockModelRenderer.
- Only the NetHandlerPlayClient observer remains transformed; it records incoming chunk-packet bursts and does not replace packet decoding or world mutation.
- Potassium's custom VBO ownership, custom draw submission, custom mesh rebuild, block/entity render culling, and renderer-side chunk scheduling hooks are not run.
- OptiFine/vanilla retain full ownership of rendering, chunk mesh compilation, and GPU submission.
- Independent client-side systems can still run where they do not take ownership of OptiFine rendering: CPU/JVM-heap pressure measurement, adaptive budgeting for OptiDex-owned optional work, world-transition bookkeeping, chunk-packet burst telemetry, and the lightweight HUD.
- Particle caps/list trimming are disabled while OptiFine is present; OptiFine keeps ownership of particle behavior and settings.
- OptiDex does not try to replace OptiFine's Fast Render, Fast Math, Smooth FPS, Smooth World, Render Regions, Smart Animations, chunk rendering, entity/block culling, or zoom controls.
- Experimental distant-entity update throttling remains off by default.

Without OptiFine, the legacy standalone renderer hooks may still be available when their settings and hardware checks allow them. The supported companion configuration is OptiFine-first.

This is a compatibility-first separation, not a promise that every OptiFine option or third-party mod combination is compatible. Test the exact OptiFine build and modpack you use.

## Optimization systems

The codebase includes Potassium-owned systems for:
- CPU tick-time and JVM-heap pressure measurement (without forced garbage collection)
- Adaptive budgets for OptiDex-owned optional maintenance work
- Multiplayer chunk-packet burst telemetry (client-side only; does not change server TPS)
- World-transition bookkeeping and conservative warm-up budgeting
- Lightweight FPS/ping and diagnostic HUD

Some custom GPU/mesh paths are intentionally disabled while OptiFine is present. Compatibility takes priority over forcing two renderers to own the same GPU resources.

## Performance profiles

| Profile | Purpose |
|---|---|
| HIGH | Strong optimization |
| MEDIUM | Balanced optimization |
| LOW | Lighter optimization with more QoL |
| PERFORMANCE | Aggressive performance-first budgets |

Profiles adjust workload budgets and feature switches. Results vary by system; compare the same world, render distance, and settings when benchmarking.

## Settings and HUD

OptiDex retains the Potassium settings interface, including:
- Master optimization switch
- Performance profiles and rendering/chunk controls
- Video Settings shortcut
- FPS and server-ping HUD
- Optional frame-time, coordinates, direction, biome, memory, and session-time display
- OptiFine retains zoom and other visual/QoL controls; OptiDex avoids duplicating them.

FPS/frame-time telemetry is used for measurement. The HUD does not promise a specific FPS improvement.

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

The JAR is generated in `build/libs/`.

## Project identity and releases

The mod display name is **Potassium: OptiDex** and the mod version label is **OptiDex**. The internal Forge mod ID and Java package remain `potassium` to preserve existing configuration and integration compatibility.

**No public release, GitHub Release, tag, or downloadable version should be published without Predex's explicit permission.** Repository development commits and CI builds are not public releases.
