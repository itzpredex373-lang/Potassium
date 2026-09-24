package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * CPU-side occlusion fallback. Results are cached for the current render frame
 * so an entity is not ray-traced repeatedly by multiple render hooks.
 */
public final class EntityOcclusionOptimizer {
    private static final Map<Entity, Boolean> frameCache = new IdentityHashMap<Entity, Boolean>();
    private static long frameId = -1L;
    private static int cachedCameraChunkX;
    private static int cachedCameraChunkZ;

    private EntityOcclusionOptimizer() {}

    public static void beginFrame() {
        Minecraft mc = Minecraft.getMinecraft();
        Entity camera = mc.getRenderViewEntity();

        int chunkX = camera == null ? 0 : ((int) Math.floor(camera.posX)) >> 4;
        int chunkZ = camera == null ? 0 : ((int) Math.floor(camera.posZ)) >> 4;

        if (frameId != RenderFrameCounter.getFrameId()
                || chunkX != cachedCameraChunkX
                || chunkZ != cachedCameraChunkZ) {
            frameId = RenderFrameCounter.getFrameId();
            cachedCameraChunkX = chunkX;
            cachedCameraChunkZ = chunkZ;
            frameCache.clear();
        }
    }

    public static boolean isVisible(Entity entity) {
        if (!PotassiumConfig.entityOcclusionCulling || entity == null) return true;

        beginFrame();
        Boolean cached = frameCache.get(entity);
        if (cached != null) return cached.booleanValue();

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null || mc.getRenderViewEntity() == null) return true;

        Entity camera = mc.getRenderViewEntity();
        Vec3 start = camera.getPositionEyes(1.0F);
        AxisAlignedBB box = entity.getEntityBoundingBox();
        if (box == null) return true;

        Vec3[] targets = new Vec3[] {
                new Vec3((box.minX + box.maxX) * 0.5D, (box.minY + box.maxY) * 0.5D, (box.minZ + box.maxZ) * 0.5D),
                new Vec3(box.minX, box.minY + (box.maxY - box.minY) * 0.5D, box.minZ),
                new Vec3(box.maxX, box.minY + (box.maxY - box.minY) * 0.5D, box.maxZ)
        };

        boolean visible = true;
        int clearSamples = 0;
        for (Vec3 target : targets) {
            MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(start, target, false, true, false);
            if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
                clearSamples++;
                continue;
            }

            BlockPos hitPos = hit.getBlockPos();
            if (hitPos == null || !mc.theWorld.getBlockState(hitPos).getBlock().isOpaqueCube()) {
                clearSamples++;
            }
        }

        if (clearSamples == 0) visible = false;
        frameCache.put(entity, Boolean.valueOf(visible));
        return visible;
    }
}
