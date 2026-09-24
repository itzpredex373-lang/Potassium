package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public final class EntityOcclusionOptimizer {
    private EntityOcclusionOptimizer() {}

    public static boolean isVisible(Entity entity) {
        if (!PotassiumConfig.entityOcclusionCulling || entity == null) return true;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.getRenderViewEntity() == null) return true;

        Entity camera = mc.getRenderViewEntity();
        Vec3 start = camera.getPositionEyes(1.0F);
        AxisAlignedBB box = entity.getEntityBoundingBox();
        if (box == null) return true;

        // Test several points so thin/partial exposure is not incorrectly hidden.
        Vec3[] targets = new Vec3[] {
                new Vec3((box.minX + box.maxX) * 0.5D, (box.minY + box.maxY) * 0.5D, (box.minZ + box.maxZ) * 0.5D),
                new Vec3(box.minX, box.minY + (box.maxY - box.minY) * 0.5D, box.minZ),
                new Vec3(box.maxX, box.minY + (box.maxY - box.minY) * 0.5D, box.maxZ)
        };

        for (Vec3 target : targets) {
            MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(start, target, false, true, false);
            if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                return true;
            }

            BlockPos hitPos = hit.getBlockPos();
            if (hitPos == null || !mc.theWorld.getBlockState(hitPos).getBlock().isOpaqueCube()) {
                return true;
            }
        }

        return false;
    }
}
