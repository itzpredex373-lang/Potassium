package com.predex.potassium.optimization.rendering;

/**
 * Part 1/2 block-rendering math helpers.
 *
 * These are cheap, allocation-free predicates that can be reused by deeper
 * rendering hooks without changing Minecraft's renderer from an event handler.
 */
public final class BlockRenderOptimizer {
    private BlockRenderOptimizer() {}

    public static boolean isChunkInRenderRadius(
            int chunkX, int chunkZ,
            int playerChunkX, int playerChunkZ,
            int renderDistanceChunks) {

        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        int radius = Math.max(2, renderDistanceChunks);

        return dx * dx + dz * dz <= radius * radius;
    }

    public static boolean isChunkCenterInDistance(
            int chunkX, int chunkZ,
            double playerX, double playerZ,
            double maxDistance) {

        double centerX = chunkX * 16.0D + 8.0D;
        double centerZ = chunkZ * 16.0D + 8.0D;
        double dx = centerX - playerX;
        double dz = centerZ - playerZ;

        return dx * dx + dz * dz <= maxDistance * maxDistance;
    }

    public static int getChunkDistanceSq(
            int chunkX, int chunkZ,
            int playerChunkX, int playerChunkZ) {

        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        return dx * dx + dz * dz;
    }
}
