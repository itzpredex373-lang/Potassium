package com.predex.potassium.optimization.rendering;

public final class RenderMath {
    private RenderMath() {}

    public static boolean withinDistanceSq(double dx, double dy, double dz,
                                           double maxDistance) {
        return dx * dx + dy * dy + dz * dz <= maxDistance * maxDistance;
    }

    public static int chunkX(double worldX) {
        return ((int) Math.floor(worldX)) >> 4;
    }

    public static int chunkZ(double worldZ) {
        return ((int) Math.floor(worldZ)) >> 4;
    }

    public static int chunkDistanceSq(int chunkX, int chunkZ,
                                      int playerChunkX, int playerChunkZ) {
        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        return dx * dx + dz * dz;
    }
}
