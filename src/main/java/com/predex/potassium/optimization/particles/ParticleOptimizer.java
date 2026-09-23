package com.predex.potassium.optimization.particles;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Part 3 particle optimization.
 *
 * Minecraft 1.8.9 has no general Forge particle-spawn event, so Potassium
 * applies the budget directly to EffectRenderer's client particle lists.
 */
public final class ParticleOptimizer {
    private static long tick;
    private static int particlesThisTick;
    private static Field particleLayersField;
    private static Field particleLayersAlphaField;

    private ParticleOptimizer() {}

    public static void beginTick() {
        tick++;
        particlesThisTick = 0;

        if (isEnabled()) {
            trimParticleLayers();
        }
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

    private static void trimParticleLayers() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.effectRenderer == null) {
            return;
        }

        try {
            initializeFields(minecraft.effectRenderer);

            int budget = PotassiumConfig.maxParticlesPerTick;
            int total = countParticles(minecraft.effectRenderer, particleLayersField)
                    + countParticles(minecraft.effectRenderer, particleLayersAlphaField);

            int excess = total - budget;
            if (excess <= 0) {
                return;
            }

            // Remove oldest entries from higher-numbered layers first.
            excess = trimArray(minecraft.effectRenderer, particleLayersAlphaField, excess);
            trimArray(minecraft.effectRenderer, particleLayersField, excess);
        } catch (Throwable ignored) {
            // Performance optimization must never crash the client.
        }
    }

    private static int countParticles(EffectRenderer renderer, Field field) throws IllegalAccessException {
        if (field == null) {
            return 0;
        }

        Object value = field.get(renderer);
        if (!(value instanceof List[])) {
            return 0;
        }

        int total = 0;
        List<?>[] layers = (List<?>[]) value;
        for (List<?> layer : layers) {
            if (layer != null) {
                total += layer.size();
            }
        }
        return total;
    }

    private static int trimArray(EffectRenderer renderer, Field field, int excess) {
        if (field == null || excess <= 0) {
            return excess;
        }

        try {
            Object value = field.get(renderer);
            if (!(value instanceof List[])) {
                return excess;
            }

            List<?>[] layers = (List<?>[]) value;

            for (int i = layers.length - 1; i >= 0 && excess > 0; i--) {
                List<?> layer = layers[i];
                if (layer == null || layer.isEmpty()) {
                    continue;
                }

                int removeCount = Math.min(excess, layer.size());

                // EntityFX entries are kept in insertion order in these lists;
                // removing from the front preferentially drops older particles.
                for (int j = 0; j < removeCount; j++) {
                    layer.remove(0);
                }

                excess -= removeCount;
            }
        } catch (Throwable ignored) {
            // Ignore incompatible internals safely.
        }

        return Math.max(0, excess);
    }

    private static void initializeFields(EffectRenderer renderer) throws IllegalAccessException {
        if (particleLayersField != null && particleLayersAlphaField != null) {
            return;
        }

        Field firstListArray = null;
        Field secondListArray = null;

        for (Field field : renderer.getClass().getDeclaredFields()) {
            Class<?> type = field.getType();

            if (!type.isArray() || !List.class.isAssignableFrom(type.getComponentType())) {
                continue;
            }

            field.setAccessible(true);

            if (firstListArray == null) {
                firstListArray = field;
            } else if (secondListArray == null) {
                secondListArray = field;
                break;
            }
        }

        if (firstListArray == null) {
            firstListArray = ReflectionHelper.findField(
                    EffectRenderer.class, "fxLayers", "field_78876_b");
        }

        if (secondListArray == null) {
            secondListArray = ReflectionHelper.findField(
                    EffectRenderer.class, "fxLayersAlpha", "field_78875_c");
        }

        particleLayersField = firstListArray;
        particleLayersAlphaField = secondListArray;
    }
}
