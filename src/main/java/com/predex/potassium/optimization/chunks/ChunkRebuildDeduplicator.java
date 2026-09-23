package com.predex.potassium.optimization.chunks;

import java.util.HashSet;
import java.util.Set;

/** Prevents the same optional chunk rebuild from being queued repeatedly. */
public final class ChunkRebuildDeduplicator {
    private final Set<Long> pending = new HashSet<Long>();

    public boolean markPending(int x, int z) {
        return pending.add(key(x, z));
    }

    public void markComplete(int x, int z) {
        pending.remove(key(x, z));
    }

    public void clear() {
        pending.clear();
    }

    public int size() {
        return pending.size();
    }

    private static long key(int x, int z) {
        return ((long) x << 32) ^ (z & 0xffffffffL);
    }
}