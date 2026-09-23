package com.predex.potassium.optimization.world;

import java.util.HashSet;
import java.util.Set;

public final class RedundantUpdateTracker {
    private final Set<Long> seen = new HashSet<Long>();

    public boolean first(int x, int y, int z) {
        return seen.add(pack(x, y, z));
    }

    public void clear() { seen.clear(); }

    public int size() { return seen.size(); }

    private static long pack(int x, int y, int z) {
        return ((long)x * 31L + y) * 31L + z;
    }
}