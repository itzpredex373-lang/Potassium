package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.IdentityHashMap;
import java.util.Map;

public final class EntityOcclusionOptimizer {
    private static final Map<Entity, Boolean> frameCache = new IdentityHashMap<Entity, Boolean>();
    private static long frameId = -1L;
    private static int cachedCameraChunkX;
    private static int cachedCameraChunkZ;
    private static int testsThisFrame;

    private EntityOcclusionOptimizer() {}

    public static void beginFrame() {
        Minecraft mc = Minecraft.getMinecraft();
        Entity camera = mc.getRenderViewEntity();

        int chunkX = camera == null ? 0 : ((int) Math.floor(camera.posX)) >> 4;
        int chunkZ = camera == null ? 0 : ((int) Math.floor(camera.posZ)) >> 4;
        long currentFrame = RenderFrameCounter.getFrameId();

        if (frameId != currentFrame
                || chunkX != cachedCameraChunkX
                || chunkZ != cachedCameraChunkZ) {
            frameId = currentFrame;
            cachedCameraChunkX = chunkX;
            cachedCameraChunkZ = chunkZ;
            frameCache.clear();
            testsThisFrame = 0;
        }
    }

    private static boolean tryAcquireTest() {
        if (testsThisFrame >= Math.max(1, PotassiumConfig.maxEntityOcclusionTestsPerFrame)) {
            return false;
        }
        testsThisFrame++;
        return true;
    }

    public static boolean isVisible(Entity entity) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.entityOcclusionCulling || entity == null) return true;

        Boolean cached = frameCache.get(entity);
        if (cached != null) return cached.booleanValue();

        Minecraft mc = Minecraft.getMinecraft();
        Entity camera = mc.getRenderViewEntity();
        if (mc.theWorld == null || camera == null || entity == camera) return true;

        AxisAlignedBB box = entity.getEntityBoundingBox();
        if (box == null) return true;

        double distanceSq = entity.getDistanceSqToEntity(camera);
        if (distanceSq < 64.0D) return true;

        Vec3 start = camera.getPositionEyes(1.0F);

        // Center sample is the cheap/common path. Extra samples are only used
        // when the center is blocked and each consumes its own frame budget.
        if (!tryAcquireTest()) return true;
        boolean visible = isSampleVisible(mc, start,
                (box.minX + box.maxX) * 0.5D,
                (box.minY + box.maxY) * 0.5D,
                (box.minZ + box.maxZ) * 0.5D);

        if (!visible && tryAcquireTest()) {
            visible = isSampleVisible(mc, start,
                    box.minX + (box.maxX - box.minX) * 0.35D,
                    box.maxY - (box.maxY - box.minY) * 0.15D,
                    box.minZ + (box.maxZ - box.minZ) * 0.35D);
        }

        if (!visible && tryAcquireTest()) {
            visible = isSampleVisible(mc, start,
                    box.maxX - (box.maxX - box.minX) * 0.35D,
                    box.minY + (box.maxY - box.minY) * 0.15D,
                    box.maxZ - (box.maxZ - box.minZ) * 0.35D);
        }

        frameCache.put(entity, Boolean.valueOf(visible));
        return visible;
    }

    private static boolean isSampleVisible(Minecraft mc, Vec3 start,
                                           double x, double y, double z) {
        Vec3 target = new Vec3(x, y, z);
        MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(
                start, target, false, true, false);

        if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return true;
        }

        BlockPos hitPos = hit.getBlockPos();
        return hitPos == null
                || !mc.theWorld.getBlockState(hitPos).getBlock().isOpaqueCube();
    }
}
