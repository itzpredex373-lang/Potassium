package com.predex.potassium.optimization.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public final class FrustumRenderOptimizer {
    private static final Frustum FRUSTUM = new Frustum();
    private static boolean frameReady;

    private FrustumRenderOptimizer() {}

    /**
     * Refreshes the vanilla 1.8.9 frustum once per render frame.
     * Entity hooks then reuse the same camera/frustum instead of rebuilding it
     * for every entity.
     */
    public static void beginFrame() {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity camera = minecraft.getRenderViewEntity();
        if (camera == null) {
            frameReady = false;
            return;
        }

        FRUSTUM.setPosition(camera.posX, camera.posY, camera.posZ);
        frameReady = true;
    }

    public static boolean isVisible(net.minecraft.util.AxisAlignedBB box) {
        if (!frameReady) beginFrame();
        if (!frameReady || box == null) return true;
        return FRUSTUM.isBoundingBoxInFrustum(box);
    }

    public static boolean isVisible(Entity entity) {
        if (entity == null) return true;
        if (!frameReady) beginFrame();
        if (!frameReady) return true;

        AxisAlignedBB box = entity.getEntityBoundingBox();
        return box == null || isVisible(box);
    }
}
