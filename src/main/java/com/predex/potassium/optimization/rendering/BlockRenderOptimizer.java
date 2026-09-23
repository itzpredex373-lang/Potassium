package com.predex.potassium.optimization.rendering;

/**
 * Block-rendering optimization helpers.
 *
 * The actual chunk/block renderer rewrite is intentionally kept separate from
 * this first safe milestone. These helpers provide cheap checks that can be
 * reused by the chunk scheduler in Part 2.
 */
public final class BlockRenderOptimizer {
    private BlockRenderOptimizer() {}

    /**
     * Returns whether a chunk is inside the configured render radius.
     */
    public static boolean isChunkInRenderRadius(
            int chunkX,
            int chunkZ,
            int playerChunkX,
            int playerChunkZ,
            int renderDistanceChunks) {

        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        int radius = Math.max(2, renderDistanceChunks);

        return dx * dx + dz * dz <= radius * radius;
    }
}
