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

    @SuppressWarnings("unchecked")
    private static void trimParticleLayers() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.effectRenderer == null) {
            return;
        }

        try {
            initializeFields(minecraft.effectRenderer);

            int remaining = PotassiumConfig.maxParticlesPerTick;
            remaining = trimArray(minecraft.effectRenderer, particleLayersField, remaining);
            trimArray(minecraft.effectRenderer, particleLayersAlphaField, remaining);
        } catch (Throwable ignored) {
            // Performance optimization must never crash the client.
        }
    }

    private static int trimArray(EffectRenderer renderer, Field field, int remaining) {
        if (field == null || remaining <= 0) {
            return remaining;
        }

        try {
            Object value = field.get(renderer);
            if (!(value instanceof List[])) {
                return remaining;
            }

            List<?>[] layers = (List<?>[]) value;

            for (int i = layers.length - 1; i >= 0 && remaining > 0; i--) {
                List<?> layer = layers[i];
                if (layer == null || layer.isEmpty()) {
                    continue;
                }

                if (layer.size() <= remaining) {
                    remaining -= layer.size();
                    continue;
                }

                int removeCount = layer.size() - remaining;
                for (int j = 0; j < removeCount; j++) {
                    layer.remove(0);
                }
                remaining = 0;
            }
        } catch (Throwable ignored) {
            // Ignore incompatible internals safely.
        }

        return Math.max(0, remaining);
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
