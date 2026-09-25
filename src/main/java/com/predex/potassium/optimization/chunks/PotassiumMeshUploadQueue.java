package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.network.MultiplayerPerformanceOptimizer;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Client-thread upload admission queue.
 *
 * Actual OpenGL/VBO ownership remains with Minecraft 1.8.9. Producers may
 * enqueue CPU-side completion callbacks from worker threads; callbacks are
 * drained only from the client/render lifecycle. A strict per-frame budget
 * prevents a large rebuild wave from becoming one long render-thread spike.
 */
public final class PotassiumMeshUploadQueue {
    private static final Queue<Runnable> queue = new ArrayDeque<Runnable>();
    private static int lastDrained;
    private static int dropped;
    private static int failed;

    private PotassiumMeshUploadQueue() {}

    public static synchronized boolean offer(Runnable upload) {
        if (upload == null) return false;
        int max = Math.max(8, PotassiumConfig.maxMeshUploadsPerFrame * 8);
        if (queue.size() >= max) {
            dropped++;
            return false;
        }
        queue.offer(upload);
        return true;
    }

    public static void drainFrame() {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.meshUploadPipeline) {
            lastDrained = 0;
            return;
        }

        int budget = MultiplayerPerformanceOptimizer.getMeshUploadBudget(
                Math.max(1, PotassiumConfig.maxMeshUploadsPerFrame));
        int drained = 0;

        while (drained < budget) {
            Runnable task;
            synchronized (PotassiumMeshUploadQueue.class) {
                task = queue.poll();
            }
            if (task == null) break;

            try {
                task.run();
            } catch (Throwable ignored) {
                failed++;
                // Fail open: a broken optional upload must never stop the
                // Minecraft client thread.
            }
            drained++;
        }

        lastDrained = drained;
    }

    public static synchronized int size() { return queue.size(); }
    public static int getLastDrained() { return lastDrained; }
    public static synchronized int getDropped() { return dropped; }
    public static synchronized int getFailed() { return failed; }

    public static synchronized void clear() {
        queue.clear();
        lastDrained = 0;
        dropped = 0;
        failed = 0;
    }
}
