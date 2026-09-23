package com.predex.potassium.optimization.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;

public final class ParticleCullingOptimizer {
    private ParticleCullingOptimizer() {}

    public static boolean shouldKeep(double x, double y, double z, double maxDistance) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) return true;
        double dx=x-mc.thePlayer.posX, dy=y-mc.thePlayer.posY, dz=z-mc.thePlayer.posZ;
        return dx*dx + dy*dy + dz*dz <= maxDistance * maxDistance;
    }

    public static boolean shouldKeep(AxisAlignedBB box, double maxDistance) {
        if (box == null) return true;
        return shouldKeep((box.minX+box.maxX)*0.5D, (box.minY+box.maxY)*0.5D,
                (box.minZ+box.maxZ)*0.5D, maxDistance);
    }
}