package com.predex.potassium.optimization.benchmark;

import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import com.predex.potassium.optimization.telemetry.PerformanceTelemetry;

/** Temporary developer diagnostics for Potassium Dev Temp Version 1. */
public final class PotassiumDevDiagnostics {
    public static final boolean ENABLED = true;
    public static volatile boolean asmEffectRenderer;
    public static volatile boolean asmTessellator;
    public static volatile boolean asmRenderGlobal;
    public static volatile boolean asmBlockModelRenderer;
    public static volatile int asmFailures;
    public static volatile long particleHookCalls;
    public static volatile long tessellatorHookCalls;
    public static volatile long chunkHookCalls;
    public static volatile long blockHookCalls;
    private static volatile int minimumFps;

    private PotassiumDevDiagnostics() {}

    public static void sampleFps(int fps) {
        if (fps > 0 && (minimumFps == 0 || fps < minimumFps)) minimumFps = fps;
    }

    public static String getProfile() {
        return PerformanceProfileManager.getActiveProfileName();
    }

    public static String getAsmStatus() {
        return (asmEffectRenderer ? "E" : "-")
                + (asmTessellator ? "T" : "-")
                + (asmRenderGlobal ? "G" : "-")
                + (asmBlockModelRenderer ? "B" : "-");
    }

    public static int getAverageFps() {
        double ms = PerformanceTelemetry.getAverageFrameMillis();
        return ms <= 0.0D ? 0 : (int) Math.round(1000.0D / ms);
    }

    public static int getMinimumFps() {
        return minimumFps;
    }

    public static int getOnePercentLowFps() {
        return (int) Math.round(PerformanceTelemetry.getOnePercentLowFps());
    }
}