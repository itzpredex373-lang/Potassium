package com.predex.potassium.optimization.chunks;

/**
 * Allocation-free chunk work planner for Part 2.
 *
 * It builds a small nearest-first candidate queue when the player changes
 * chunk. The queue is intentionally separate from Minecraft's RenderGlobal
 * internals so it can later be connected through a safe hook/coremod.
 */
public final class ChunkWorkQueue {
    private static final int MAX_CANDIDATES = 256;

    private final int[] chunkX = new int[MAX_CANDIDATES];
    private final int[] chunkZ = new int[MAX_CANDIDATES];
    private final int[] priority = new int[MAX_CANDIDATES];
    private int size;

    public void clear() {
        size = 0;
    }

    public void rebuild(int playerChunkX, int playerChunkZ, int radius) {
        clear();

        int safeRadius = Math.max(2, Math.min(radius, 32));
        int maxDistanceSq = safeRadius * safeRadius;

        for (int dz = -safeRadius; dz <= safeRadius; dz++) {
            for (int dx = -safeRadius; dx <= safeRadius; dx++) {
                int distanceSq = dx * dx + dz * dz;
                if (distanceSq > maxDistanceSq) {
                    continue;
                }
                insert(playerChunkX + dx, playerChunkZ + dz, distanceSq);
            }
        }
    }

    private void insert(int x, int z, int distanceSq) {
        if (size == MAX_CANDIDATES && distanceSq >= priority[size - 1]) {
            return;
        }

        int index = size < MAX_CANDIDATES ? size : MAX_CANDIDATES - 1;

        while (index > 0 && priority[index - 1] > distanceSq) {
            if (index < MAX_CANDIDATES) {
                chunkX[index] = chunkX[index - 1];
                chunkZ[index] = chunkZ[index - 1];
                priority[index] = priority[index - 1];
            }
            index--;
        }

        chunkX[index] = x;
        chunkZ[index] = z;
        priority[index] = distanceSq;

        if (size < MAX_CANDIDATES) {
            size++;
        }
    }

    public int size() {
        return size;
    }

    public int getChunkX(int index) {
        return chunkX[index];
    }

    public int getChunkZ(int index) {
        return chunkZ[index];
    }

    public int getPriority(int index) {
        return priority[index];
    }
}
