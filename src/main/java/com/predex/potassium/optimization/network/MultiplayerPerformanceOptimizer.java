package com.predex.potassium.optimization.network;

import com.predex.potassium.config.PotassiumConfig;

/**
 * Client-side multiplayer workload governor for Minecraft 1.8.9.
 *
 * This does not and cannot change a remote server's TPS. It reacts to
 * server->client chunk/update bursts so those bursts do not become large
 * render-thread spikes on the client.
 */
public final class MultiplayerPerformanceOptimizer {
    private static long lastChunkPacketNanos;
    private static int chunkPacketsInBurst;
    private static int totalChunkPackets;
    private static boolean multiplayer;

    private MultiplayerPerformanceOptimizer() {}

    public static void onWorldJoin() {
        multiplayer = true;
        lastChunkPacketNanos = System.nanoTime();
        chunkPacketsInBurst = 0;
    }

    public static void onDisconnect() {
        multiplayer = false;
        lastChunkPacketNanos = 0L;
        chunkPacketsInBurst = 0;
    }

    public static void onChunkPacket() {
        multiplayer = true;
        long now = System.nanoTime();
        if (lastChunkPacketNanos == 0L || now - lastChunkPacketNanos > 150_000_000L) {
            chunkPacketsInBurst = 0;
        }
        lastChunkPacketNanos = now;
        chunkPacketsInBurst++;
        totalChunkPackets++;
    }

    public static void onClientTick() {
        if (!multiplayer) return;
        long now = System.nanoTime();
        if (lastChunkPacketNanos != 0L && now - lastChunkPacketNanos > 250_000_000L) {
            chunkPacketsInBurst = 0;
        }
    }

    /**
     * During chunk-packet bursts, keep render-thread upload work at one mesh
     * per frame. This trades a little upload latency for much better frame
     * pacing while joining, teleporting, or moving through new terrain.
     */
    public static int getMeshUploadBudget(int configuredBudget) {
        int base = Math.max(1, configuredBudget);
        if (!PotassiumConfig.smoothFps || !multiplayer) return base;

        long now = System.nanoTime();
        boolean recent = lastChunkPacketNanos != 0L
                && now - lastChunkPacketNanos < 250_000_000L;
        if (recent && chunkPacketsInBurst >= 2) {
            return 1;
        }
        return base;
    }

    public static boolean isMultiplayerBurst() {
        long now = System.nanoTime();
        return multiplayer
                && lastChunkPacketNanos != 0L
                && now - lastChunkPacketNanos < 250_000_000L
                && chunkPacketsInBurst >= 2;
    }

    public static int getChunkPacketsInBurst() {
        return chunkPacketsInBurst;
    }

    public static int getTotalChunkPackets() {
        return totalChunkPackets;
    }

    public static void resetStats() {
        totalChunkPackets = 0;
        chunkPacketsInBurst = 0;
        lastChunkPacketNanos = 0L;
    }
}
