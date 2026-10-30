# Potassium: OptiDex

A minimal Minecraft Java Edition 1.8.9 Forge mod foundation.

## Target platform

- Minecraft Java Edition 1.8.9
- Minecraft Forge 11.15.1.2318
- Java 8
- ForgeGradle 2.1 / Gradle wrapper 2.14.1

## Cleanup status

The legacy experimental optimization implementation has been removed rather than presented as working FPS improvements.

Removed from the source tree:
- Potassium-owned renderer and render hooks
- Custom chunk mesh engine, mesh cache, GPU region/VBO and upload pipeline
- Chunk scheduling, async preparation, streaming and rebuild queues
- Entity rendering/update and occlusion optimizers
- Particle culling, scheduling, caps and pools
- CPU/memory pressure controllers and adaptive quality systems
- Benchmark/frame-time telemetry and monitoring
- Multiplayer chunk-packet telemetry
- World-transition and world-update schedulers
- Old performance profiles and obsolete configuration
- Coremod transformer and bytecode hooks
- Custom settings, HUD and duplicate video controls

OptiFine/vanilla are left in control of rendering, chunk compilation, GPU submission, particles and visual settings. This cleaned build currently contains only the Forge mod entry point; **it does not currently claim to provide FPS optimizations**. Independent optimization features may be reintroduced only when they are demonstrably useful, do not duplicate OptiFine, and can be validated on Minecraft 1.8.9.

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

No public release, tag, or downloadable version is created by this cleanup.
