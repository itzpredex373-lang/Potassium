package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.benchmark.PerformanceTelemetry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * CPU-side section graph for the Sodium-style render pipeline.
 *
 * It does not replace vanilla RenderChunk GL storage yet; instead it gives
 * Potassium a stable section-level visibility/dirty model and a cheap place
 * to coalesce rebuild work before a future custom mesh backend takes over.
 */
public final class PotassiumRenderSectionManager {
    private static final Map<Long, PotassiumRenderSection> sections =
            new HashMap<Long, PotassiumRenderSection>();
    private static long frame;
    private static int visibleSections;
    private static int observedSections;

    private PotassiumRenderSectionManager() {}

    public static void observe(RenderChunk chunk) {
        if (chunk == null) return;
        try {
            BlockPos pos = chunk.getPosition();
            if (pos == null) return;
            PotassiumRenderSection section = getOrCreate(
                    pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
            section.seen(frame);
            observedSections++;
        } catch (Throwable ignored) {
        }
    }

    public static void markDirty(RenderChunk chunk) {
        if (chunk == null) return;
        try {
            BlockPos pos = chunk.getPosition();
            if (pos != null) {
                getOrCreate(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4).markDirty();
            }
        } catch (Throwable ignored) {
        }
    }

    public static void beginFrame() {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.renderSections) {
            return;
        }

        frame++;
        visibleSections = 0;
        observedSections = 0;

        Minecraft mc = Minecraft.getMinecraft();
        Entity camera = mc.getRenderViewEntity();
        if (camera == null) return;

        int cameraChunkX = ((int) Math.floor(camera.posX)) >> 4;
        int cameraChunkZ = ((int) Math.floor(camera.posZ)) >> 4;
        int radius = Math.max(2, PotassiumConfig.chunkUpdateRadius + 2);
        int minX = cameraChunkX - radius;
        int maxX = cameraChunkX + radius;
        int minZ = cameraChunkZ - radius;
        int maxZ = cameraChunkZ + radius;

        Iterator<Map.Entry<Long, PotassiumRenderSection>> it = sections.entrySet().iterator();
        while (it.hasNext()) {
            PotassiumRenderSection section = it.next().getValue();
            if (section.getChunkX() < minX || section.getChunkX() > maxX
                    || section.getChunkZ() < minZ || section.getChunkZ() > maxZ) {
                section.setVisible(false);
                if (frame - section.getLastSeenFrame() > 120L) {
                    it.remove();
                }
                continue;
            }

            AxisAlignedBB box = section.bounds();
            boolean visible = FrustumSectionTest.isVisible(box);
            section.setVisible(visible);
            if (visible) {
                visibleSections++;
                section.seen(frame);
            }
        }
    }

    private static PotassiumRenderSection getOrCreate(int chunkX, int sectionY, int chunkZ) {
        long key = PotassiumRenderSection.key(chunkX, sectionY, chunkZ);
        PotassiumRenderSection section = sections.get(key);
        if (section == null) {
            section = new PotassiumRenderSection(chunkX, sectionY, chunkZ);
            sections.put(key, section);
        }
        return section;
    }

    public static boolean shouldRender(RenderChunk chunk) {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.renderSections || chunk == null) {
            return true;
        }

        try {
            BlockPos pos = chunk.getPosition();
            if (pos == null) return true;
            PotassiumRenderSection section = getOrCreate(
                    pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4);
            return section.isVisible();
        } catch (Throwable ignored) {
            return true;
        }
    }

    public static int getVisibleSections() { return visibleSections; }
    public static int getObservedSections() { return observedSections; }
    public static int getSectionCount() { return sections.size(); }

    public static void clear() {
        sections.clear();
        visibleSections = 0;
        observedSections = 0;
    }

    private static final class FrustumSectionTest {
        private static boolean isVisible(AxisAlignedBB box) {
            try {
                return com.predex.potassium.optimization.rendering.FrustumRenderOptimizer
                        .isVisible(box);
            } catch (Throwable ignored) {
                return true;
            }
        }
    }
}
