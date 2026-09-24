package com.predex.potassium.optimization.world;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.system.CpuOptimizer;
import net.minecraft.client.Minecraft;

public final class SmoothWorldOptimizer {
    private SmoothWorldOptimizer() {}

    public static boolean allowOptionalWork() {
        if (!PerformanceManager.isOptimizationEnabled() || !PotassiumConfig.smoothWorld) return true;

        Minecraft minecraft = Minecraft.getMinecraft();
        if (!minecraft.isIntegratedServerRunning()) return true;

        return CpuOptimizer.shouldRunOptionalWork();
    }
}