package com.predex.potassium.optimization.chunks;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;

import java.nio.ByteBuffer;

/**
 * Captures vanilla-compatible BLOCK vertex data on the build thread and
 * schedules the actual OpenGL upload for the render thread.
 */
public final class PotassiumGpuMeshUpload {
    private PotassiumGpuMeshUpload() {}

    public static boolean queue(RenderChunk chunk,
                                EnumWorldBlockLayer layer,
                                WorldRenderer renderer,
                                long revision) {
        if (chunk == null || layer == null || renderer == null) return false;

        final int vertexCount = renderer.getVertexCount();
        final int bytes = vertexCount * 28;

        if (vertexCount <= 0 || bytes <= 0) {
            return PotassiumMeshUploadQueue.offer(new Runnable() {
                @Override
                public void run() {
                    PotassiumGpuRegionManager.markEmpty(chunk, layer, revision);
                }
            });
        }

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
                PotassiumGpuRegionManager.upload(
                        chunk, layer, copy, vertexCount, revision);
            }
        });
    }
}
