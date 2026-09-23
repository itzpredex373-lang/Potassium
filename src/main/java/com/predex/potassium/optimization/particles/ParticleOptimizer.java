package com.predex.potassium.optimization.particles;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Part 3 particle budget.
 *
 * This budget is a reusable guard for particle-spawn/render hooks. Minecraft
 * 1.8.9 does not expose a general Forge particle-spawn event, so this class
 * does not fake an interception that does not exist. A later client hook can
 * call tryAcquire() before accepting optional particle work.
 */
public final class ParticleOptimizer {
    private static long tick;
    private static int particlesThisTick;

    private ParticleOptimizer() {}

    public static void beginTick() {
        tick++;
        particlesThisTick = 0;
    }

    public static boolean isEnabled() {
        return PotassiumConfig.enabled && PotassiumConfig.reduceParticles;
    }

    public static boolean tryAcquire() {
        if (!isEnabled()) {
            return true;
        }

        if (particlesThisTick >= PotassiumConfig.maxParticlesPerTick) {
            return false;
        }

        particlesThisTick++;
        return true;
    }

    public static int getParticlesThisTick() {
        return particlesThisTick;
    }

    public static long getTick() {
        return tick;
    }
}
