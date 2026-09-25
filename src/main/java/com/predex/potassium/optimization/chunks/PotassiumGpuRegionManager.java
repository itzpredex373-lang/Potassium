package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.compat.CompatibilityManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Potassium-owned GPU mesh storage.
 *
 * Chunks are grouped into 16x16 chunk regions for lifecycle ownership, while
 * each chunk/layer owns a reusable VBO slot. This is deliberately conservative:
 * it replaces the draw submission and VBO ownership, but keeps Minecraft's
 * vanilla BLOCK vertex layout and texture state for compatibility.
 */
public final class PotassiumGpuRegionManager {
    private static final int REGION_SHIFT = 4;
    private static final int LAYERS = EnumWorldBlockLayer.values().length;

    private static final Map<Long, Region> regions = new java.util.HashMap<Long, Region>();
    private static final Map<RenderChunk, ChunkGpu> chunks =
            Collections.synchronizedMap(new IdentityHashMap<RenderChunk, ChunkGpu>());

    private static volatile long uploads;
    private static volatile long uploadBytes;
    private static volatile long drawCalls;
    private static volatile long drawnVertices;
    private static volatile long failures;

    private static Field renderChunksField;
    private static boolean renderChunksFieldResolved;

    private PotassiumGpuRegionManager() {}

    public static boolean enabled() {
        return PerformanceManager.isOptimizationEnabled()
                && PotassiumConfig.customGpuRenderer
                && PotassiumConfig.customDrawSubmission
                && PotassiumConfig.meshUploadPipeline
                && CompatibilityManager.allowCustomRenderer();
    }

    /**
     * Must be called only on the client/render thread.
     */
    public static void upload(RenderChunk chunk,
                              EnumWorldBlockLayer layer,
                              ByteBuffer data,
                              int vertexCount,
                              long revision) {
        if (!enabled() || chunk == null || layer == null) {
            return;
        }

        try {
            Region region = regionFor(chunk);
            ChunkGpu gpu = chunks.get(chunk);
            if (gpu == null) {
                gpu = new ChunkGpu(region);
                chunks.put(chunk, gpu);
                region.chunks.put(chunk, gpu);
            }

            int id = layer.ordinal();
            Mesh mesh = gpu.meshes[id];

            if (vertexCount <= 0 || data == null || !data.hasRemaining()) {
                deleteMesh(mesh);
                mesh.vbo = 0;
                mesh.vertexCount = 0;
                mesh.revision = revision;
                mesh.ready = true;
                return;
            }

            if (mesh.vbo == 0) {
                mesh.vbo = OpenGlHelper.glGenBuffers();
            }

            OpenGlHelper.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.vbo);
            OpenGlHelper.glBufferData(GL15.GL_ARRAY_BUFFER, data, GL15.GL_STATIC_DRAW);
            OpenGlHelper.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            mesh.vertexCount = vertexCount;
            mesh.revision = revision;
            mesh.ready = true;

            uploads++;
            uploadBytes += data.remaining();
        } catch (Throwable failure) {
            failures++;
            CompatibilityManager.recordRendererFailure();
        }
    }

    /**
     * Marks an empty layer as ready without allocating a GL buffer.
     */
    public static void markEmpty(RenderChunk chunk,
                                 EnumWorldBlockLayer layer,
                                 long revision) {
        upload(chunk, layer, null, 0, revision);
    }

    /**
     * Returns true only when every chunk currently queued by VboRenderList has
     * a matching custom mesh. If anything is missing/stale, vanilla rendering
     * remains in control for the whole layer.
     */
    public static boolean renderLayer(VboRenderList container,
                                      EnumWorldBlockLayer layer) {
        if (!enabled() || container == null || layer == null) {
            return false;
        }

        try {
            List<RenderChunk> renderList = getRenderChunks(container);
            if (renderList == null || renderList.isEmpty()) {
                return false;
            }

            List<RenderChunk> snapshot = new ArrayList<RenderChunk>(renderList);
            for (RenderChunk chunk : snapshot) {
                if (chunk == null || !isReady(chunk, layer)) {
                    return false;
                }
            }

            for (RenderChunk chunk : snapshot) {
                draw(container, chunk, layer);
            }

            // Match VboRenderList's post-layer cleanup so the transformed
            // method can safely return before its vanilla body executes.
            OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.resetColor();
            renderList.clear();

            return true;
        } catch (Throwable failure) {
            failures++;
            CompatibilityManager.recordRendererFailure();
            return false;
        }
    }

    private static boolean isReady(RenderChunk chunk, EnumWorldBlockLayer layer) {
        ChunkGpu gpu = chunks.get(chunk);
        if (gpu == null) return false;

        Mesh mesh = gpu.meshes[layer.ordinal()];
        return mesh.ready && mesh.revision == PotassiumChunkMeshCache.getRevision(chunk);
    }

    private static void draw(VboRenderList container,
                             RenderChunk chunk,
                             EnumWorldBlockLayer layer) {
        ChunkGpu gpu = chunks.get(chunk);
        Mesh mesh = gpu.meshes[layer.ordinal()];
        if (!mesh.ready || mesh.vertexCount <= 0 || mesh.vbo == 0) {
            return;
        }

        GlStateManager.pushMatrix();
        container.preRenderChunk(chunk);
        chunk.multModelviewMatrix();

        OpenGlHelper.glBindBuffer(GL15.GL_ARRAY_BUFFER, mesh.vbo);
        setupBlockPointers();
        GL11.glDrawArrays(GL11.GL_QUADS, 0, mesh.vertexCount);
        OpenGlHelper.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GlStateManager.popMatrix();

        drawCalls++;
        drawnVertices += mesh.vertexCount;
    }

    /**
     * Minecraft 1.8.9 BLOCK vertex layout:
     * position @ 0, color @ 12, texture @ 16, lightmap @ 24, stride 28.
     */
    private static void setupBlockPointers() {
        GL11.glVertexPointer(3, GL11.GL_FLOAT, 28, 0L);
        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 28, 12L);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 28, 16L);

        OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glTexCoordPointer(2, GL11.GL_SHORT, 28, 24L);
        OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @SuppressWarnings("unchecked")
    private static List<RenderChunk> getRenderChunks(VboRenderList container) throws IllegalAccessException {
        if (!renderChunksFieldResolved) {
            synchronized (PotassiumGpuRegionManager.class) {
                if (!renderChunksFieldResolved) {
                    Class<?> type = container.getClass();
                    while (type != null) {
                        for (Field field : type.getDeclaredFields()) {
                            if (List.class.isAssignableFrom(field.getType())) {
                                field.setAccessible(true);
                                renderChunksField = field;
                                break;
                            }
                        }
                        if (renderChunksField != null) break;
                        type = type.getSuperclass();
                    }
                    renderChunksFieldResolved = true;
                }
            }
        }

        if (renderChunksField == null) return null;
        return (List<RenderChunk>) renderChunksField.get(container);
    }

    private static Region regionFor(RenderChunk chunk) {
        int chunkX = chunk.getPosition().getX() >> 4;
        int chunkZ = chunk.getPosition().getZ() >> 4;
        long key = (((long) (chunkX >> REGION_SHIFT)) << 32)
                ^ ((chunkZ >> REGION_SHIFT) & 0xFFFFFFFFL);

        Region region = regions.get(key);
        if (region == null) {
            region = new Region(key);
            regions.put(key, region);
        }
        return region;
    }

    public static void remove(RenderChunk chunk) {
        if (chunk == null) return;
        ChunkGpu gpu = chunks.remove(chunk);
        if (gpu == null) return;

        for (Mesh mesh : gpu.meshes) {
            deleteMesh(mesh);
        }

        gpu.region.chunks.remove(chunk);
        if (gpu.region.chunks.isEmpty()) {
            regions.remove(gpu.region.key);
        }
    }

    /**
     * Must be called on the render thread while an OpenGL context is current.
     */
    public static void clear() {
        for (ChunkGpu gpu : new ArrayList<ChunkGpu>(chunks.values())) {
            for (Mesh mesh : gpu.meshes) {
                deleteMesh(mesh);
            }
        }
        chunks.clear();
        regions.clear();
    }

    private static void deleteMesh(Mesh mesh) {
        if (mesh == null || mesh.vbo == 0) return;
        try {
            OpenGlHelper.glDeleteBuffers(mesh.vbo);
        } catch (Throwable ignored) {
        }
        mesh.vbo = 0;
        mesh.vertexCount = 0;
        mesh.ready = false;
    }

    public static long getUploads() { return uploads; }
    public static long getUploadBytes() { return uploadBytes; }
    public static long getDrawCalls() { return drawCalls; }
    public static long getDrawnVertices() { return drawnVertices; }
    public static long getFailures() { return failures; }
    public static int getRegionCount() { return regions.size(); }
    public static int getChunkCount() { return chunks.size(); }

    public static void resetStats() {
        uploads = 0L;
        uploadBytes = 0L;
        drawCalls = 0L;
        drawnVertices = 0L;
        failures = 0L;
    }

    private static final class Region {
        private final long key;
        private final Map<RenderChunk, ChunkGpu> chunks =
                new IdentityHashMap<RenderChunk, ChunkGpu>();

        private Region(long key) {
            this.key = key;
        }
    }

    private static final class ChunkGpu {
        private final Region region;
        private final Mesh[] meshes = new Mesh[LAYERS];

        private ChunkGpu(Region region) {
            this.region = region;
            for (int i = 0; i < meshes.length; i++) {
                meshes[i] = new Mesh();
            }
        }
    }

    private static final class Mesh {
        private int vbo;
        private int vertexCount;
        private long revision;
        private boolean ready;
    }
}
