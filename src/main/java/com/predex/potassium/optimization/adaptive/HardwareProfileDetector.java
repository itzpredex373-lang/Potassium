package com.predex.potassium.optimization.adaptive;

/**
 * Lightweight hardware classification for Java/launcher environments.
 *
 * This deliberately uses only stable JVM information available on Java 8:
 * processor count and maximum heap. It is a hint for diagnostics and adaptive
 * defaults, not a claim about the physical device's full capabilities.
 */
public final class HardwareProfileDetector {
    public enum Tier {
        HIGH,
        MEDIUM,
        LOW,
        VERY_LOW
    }

    private HardwareProfileDetector() {}

    public static int getProcessorCount() {
        return Math.max(1, Runtime.getRuntime().availableProcessors());
    }

    public static long getMaxHeapMb() {
        return Math.max(1L, Runtime.getRuntime().maxMemory() / (1024L * 1024L));
    }

    public static Tier detectTier() {
        int processors = getProcessorCount();
        long heapMb = getMaxHeapMb();

        if (processors <= 2 || heapMb <= 768L) {
            return Tier.VERY_LOW;
        }

        if (processors <= 4 || heapMb <= 1536L) {
            return Tier.LOW;
        }

        if (processors <= 6 || heapMb <= 3072L) {
            return Tier.MEDIUM;
        }

        return Tier.HIGH;
    }

    /**
     * Conservative cap for optional Potassium workload.
     * This never changes vanilla gameplay rules; it only limits optional work.
     */
    public static int getOptionalWorkCapPercent() {
        switch (detectTier()) {
            case VERY_LOW:
                return 70;
            case LOW:
                return 85;
            case MEDIUM:
                return 100;
            case HIGH:
            default:
                return 100;
        }
    }

    public static String getTierName() {
        return detectTier().name();
    }
}