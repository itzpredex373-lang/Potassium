package com.predex.potassium.optimization.adaptive;

public final class HardwareWorkScaler {
    private HardwareWorkScaler() {}

    public static int scale(int configured) {
        int base = Math.max(1, configured);
        switch (HardwareProfileDetector.detectTier()) {
            case VERY_LOW: return Math.max(1, base * 50 / 100);
            case LOW: return Math.max(1, base * 70 / 100);
            case MEDIUM: return Math.max(1, base * 90 / 100);
            default: return base;
        }
    }
}