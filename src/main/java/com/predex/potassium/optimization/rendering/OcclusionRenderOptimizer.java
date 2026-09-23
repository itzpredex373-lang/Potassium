package com.predex.potassium.optimization.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

/**
 * Conservative occlusion helper. It never hides an entity when the camera
 * or bounding box is unavailable, so false positives fail open.
 */
public final class OcclusionRenderOptimizer {
    private OcclusionRenderOptimizer() {}

    public static boolean isPotentiallyVisible(Entity entity) {
        if (entity == null) return true;
        Minecraft mc = Minecraft.getMinecraft();
        Entity camera = mc.getRenderViewEntity();
        if (camera == null) return true;
        AxisAlignedBB box = entity.getEntityBoundingBox();
        if (box == null) return true;

        // Cheap screen/frustum gate. True occlusion requires renderer/depth
        // integration and is intentionally left to a later core hook.
        double cx = (box.minX + box.maxX) * 0.5D;
        double cy = (box.minY + box.maxY) * 0.5D;
        double cz = (box.minZ + box.maxZ) * 0.5D;
        double dx = cx - camera.posX, dy = cy - camera.posY, dz = cz - camera.posZ;
        return dx * dx + dy * dy + dz * dz <= 256.0D * 256.0D
                || !mc.gameSettings.fancyGraphics;
    }
}