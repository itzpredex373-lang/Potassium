package com.predex.potassium.optimization.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public final class FrustumRenderOptimizer {
    private static final Frustum FRUSTUM = new Frustum();

    private FrustumRenderOptimizer() {}

    public static boolean isVisible(Entity entity) {
        if (entity == null) {
            return true;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        Entity camera = minecraft.getRenderViewEntity();
        if (camera == null) {
            return true;
        }

        FRUSTUM.setPosition(camera.posX, camera.posY, camera.posZ);

        AxisAlignedBB box = entity.getEntityBoundingBox();
        return box == null || FRUSTUM.isBoundingBoxInFrustum(box);
    }
}
