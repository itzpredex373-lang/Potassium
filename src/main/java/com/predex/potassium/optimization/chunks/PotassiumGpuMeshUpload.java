package com.predex.potassium.optimization.chunks;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumWorldBlockLayer;

import java.nio.ByteBuffer;

/**
 * Captures vanilla-compatible BLOCK vertex data on the build thread and
 * schedules the actual OpenGL upload for the render thread.
 */
public final class PotassiumGpuMeshUpload {
    private static final int BLOCK_VERTEX_STRIDE = 28;

    private PotassiumGpuMeshUpload() {}

    public static boolean queue(RenderChunk chunk,
                                EnumWorldBlockLayer layer,
                                WorldRenderer renderer,
                                long revision) {
        if (chunk == null || layer == null || renderer == null) return false;

        // The custom VBO path is intentionally limited to the exact format
        // emitted by PotassiumRealChunkMeshEngine. Anything else must fall
        // back to Minecraft's renderer instead of risking corrupted state.
        if (renderer.getVertexFormat() != DefaultVertexFormats.BLOCK
                || renderer.getDrawMode() != 7) {
            return false;
        }

        final int vertexCount = renderer.getVertexCount();
        if (vertexCount <= 0) {
            return PotassiumMeshUploadQueue.offer(new Runnable() {
                @Override
                public void run() {
                    if (PotassiumChunkMeshCache.getRevision(chunk) != revision) return;
                    PotassiumGpuRegionManager.markEmpty(chunk, layer, revision);
                }
            });
        }

        if (vertexCount > Integer.MAX_VALUE / BLOCK_VERTEX_STRIDE) return false;

        final int bytes = vertexCount * BLOCK_VERTEX_STRIDE;
        final ByteBuffer source = renderer.getByteBuffer();
        if (source == null || source.capacity() < bytes) return false;

        final ByteBuffer copy = ByteBuffer.allocateDirect(bytes);
        ByteBuffer duplicate = source.duplicate();
        duplicate.clear();
        duplicate.limit(bytes);
        copy.put(duplicate);
        copy.flip();

        return PotassiumMeshUploadQueue.offer(new Runnable() {
            @Override
            public void run() {
                // A chunk may have been invalidated after this CPU build was
                // queued. Do not upload stale data; vanilla will rebuild it.
                if (PotassiumChunkMeshCache.getRevision(chunk) != revision) return;

                PotassiumGpuRegionManager.upload(
                        chunk, layer, copy, vertexCount, revision);
            }
        });
    }
}
