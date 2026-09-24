package com.predex.potassium.optimization.rendering;

import java.util.HashSet;
import java.util.Set;

public final class DisplayListOptimizer {
    private static final Set<Integer> completedRegions = new HashSet<Integer>();
    private DisplayListOptimizer() {}
    public static boolean isReusable(int regionKey) {
        return completedRegions.contains(Integer.valueOf(regionKey));
    }
    public static void markComplete(int regionKey) {
        completedRegions.add(Integer.valueOf(regionKey));
    }
    public static void invalidate(int regionKey) {
        completedRegions.remove(Integer.valueOf(regionKey));
    }
    public static void clear() { completedRegions.clear(); }
}
