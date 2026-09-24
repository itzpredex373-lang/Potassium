package com.predex.potassium.optimization.world;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.system.CpuOptimizer;
import net.minecraft.client.Minecraft;

/**
 * Distributes Potassium-owned optional work in local worlds instead of adding
 * large bursts on top of the integrated server tick.
 */
public final class SmoothWorldOptimizer {
    private SmoothWorldOptimizer() {}

    public static boolean allowOptionalWork() {
        if (!PotassiumConfig.enabled || !PotassiumConfig.smoothWorld) return true;

        Minecraft minecraft = Minecraft.getMinecraft();
        if (!minecraft.isIntegratedServerRunning()) return true;

        return CpuOptimizer.shouldRunOptionalWork();
    }
}
