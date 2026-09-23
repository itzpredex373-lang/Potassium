package com.predex.potassium.optimization.benchmark;

public final class StabilityGuard {
    private static boolean degraded;
    private static int failures;

    private StabilityGuard() {}

    public static void reportFailure() {
        failures++;
        if (failures >= 3) degraded = true;
    }

    public static void reportSuccess() {
        if (failures > 0) failures--;
        if (failures == 0) degraded = false;
    }

    public static boolean isDegraded() { return degraded; }
    public static int getFailures() { return failures; }

    public static boolean allowOptionalWork() {
        return !degraded;
    }
}