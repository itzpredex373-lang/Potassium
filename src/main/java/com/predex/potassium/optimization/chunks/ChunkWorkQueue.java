package com.predex.potassium.optimization.chunks;

public final class ChunkWorkQueue {
    /*
     * A radius of 32 contains at most 4225 positions. Keeping the full
     * candidate set avoids silently dropping useful chunks on higher profiles.
     * A binary min-heap keeps rebuild insertion O(n log n) instead of O(n^2).
     */
    private static final int MAX_CANDIDATES = 4225;

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
                if (distanceSq <= maxDistanceSq) {
                    insert(playerChunkX + dx, playerChunkZ + dz, distanceSq);
                }
            }
        }
    }

    private void insert(int x, int z, int distanceSq) {
        if (size >= MAX_CANDIDATES) return;

        int index = size++;
        chunkX[index] = x;
        chunkZ[index] = z;
        priority[index] = distanceSq;

        while (index > 0) {
            int parent = (index - 1) >>> 1;
            if (priority[parent] <= priority[index]) break;
            swap(parent, index);
            index = parent;
        }
    }

    private void swap(int a, int b) {
        int x = chunkX[a];
        int z = chunkZ[a];
        int p = priority[a];

        chunkX[a] = chunkX[b];
        chunkZ[a] = chunkZ[b];
        priority[a] = priority[b];

        chunkX[b] = x;
        chunkZ[b] = z;
        priority[b] = p;
    }

    public boolean hasNext() {
        return size > 0;
    }

    public int nextChunkX() {
        return chunkX[0];
    }

    public int nextChunkZ() {
        return chunkZ[0];
    }

    public int nextPriority() {
        return priority[0];
    }

    public void advance() {
        if (size <= 0) return;

        int last = --size;
        if (last <= 0) return;

        chunkX[0] = chunkX[last];
        chunkZ[0] = chunkZ[last];
        priority[0] = priority[last];

        int root = 0;
        while (true) {
            int left = (root << 1) + 1;
            if (left >= size) break;

            int right = left + 1;
            int smallest = left;
            if (right < size && priority[right] < priority[left]) {
                smallest = right;
            }

            if (priority[root] <= priority[smallest]) break;
            swap(root, smallest);
            root = smallest;
        }
    }

    public int size() {
        return size;
    }

    public int remaining() {
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
