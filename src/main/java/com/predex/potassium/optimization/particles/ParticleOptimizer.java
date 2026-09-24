package com.predex.potassium.optimization.particles;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.system.CpuOptimizer;
import com.predex.potassium.optimization.adaptive.DynamicQualityController;
import com.predex.potassium.optimization.system.MemoryOptimizer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;

public final class ParticleOptimizer {
    private static long tick;
    private static int particlesThisTick;
    private static Field particleLayersField;

    private ParticleOptimizer() {}

    public static void beginTick() {
        tick++;
        particlesThisTick = 0;

        if (isEnabled() && CpuOptimizer.shouldRunOptionalWork()) {
            trimParticleLayers(getEffectiveBudget());
        }
    }

    public static boolean isEnabled() {
        return PerformanceManager.isOptimizationEnabled() && PotassiumConfig.reduceParticles;
    }

    public static boolean tryAcquire() {
        if (!isEnabled()) {
            return true;
        }

        int budget = getEffectiveBudget();

        if (particlesThisTick >= budget) {
            return false;
        }

        particlesThisTick++;
        return true;
    }

    public static int getEffectiveBudget() {
        int configured = Math.max(16, PotassiumConfig.maxParticlesPerTick);

        if (!PotassiumConfig.adaptivePerformance) {
            return configured;
        }

        int multiplier = MemoryOptimizer.getParticleBudgetPercent();
        return Math.max(16, DynamicQualityController.scaleBudget(configured * multiplier / 100));
    }

    public static int getParticlesThisTick() {
        return particlesThisTick;
    }

    public static long getTick() {
        return tick;
    }

    private static void trimParticleLayers(int budget) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.effectRenderer == null) {
            return;
        }

        try {
            Field layersField = getParticleLayersField();
            if (layersField == null) {
                return;
            }

            Object value = layersField.get(minecraft.effectRenderer);
            if (!(value instanceof List[][])) {
                return;
            }

            List<?>[][] layers = (List<?>[][]) value;
            int total = 0;

            for (List<?>[] layerGroup : layers) {
                if (layerGroup == null) continue;
                for (List<?> layer : layerGroup) {
                    if (layer != null) total += layer.size();
                }
            }

            int excess = total - budget;
            if (excess <= 0) return;

            for (int group = layers.length - 1; group >= 0 && excess > 0; group--) {
                List<?>[] layerGroup = layers[group];
                if (layerGroup == null) continue;

                for (int mode = layerGroup.length - 1; mode >= 0 && excess > 0; mode--) {
                    List<?> layer = layerGroup[mode];
                    if (layer == null || layer.isEmpty()) continue;

                    int removeCount = Math.min(excess, layer.size());
                    layer.subList(0, removeCount).clear();
                    excess -= removeCount;
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static Field getParticleLayersField() {
        if (particleLayersField != null) return particleLayersField;

        try {
            particleLayersField = ReflectionHelper.findField(
                    EffectRenderer.class, "fxLayers", "field_78876_b");
            particleLayersField.setAccessible(true);
            return particleLayersField;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
