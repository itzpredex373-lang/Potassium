package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import com.predex.potassium.optimization.network.MultiplayerPerformanceOptimizer;
import com.predex.potassium.optimization.world.WorldTransitionOptimizer;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Client-thread upload admission queue.
 *
 * CPU-side mesh completion callbacks may be produced by worker threads, but
 * OpenGL/VBO work is admitted only from the render lifecycle. Each queued task
 * is tagged with the current world generation so a world/lobby transition
 * cannot execute stale GPU work from the previous world.
 */
public final class PotassiumMeshUploadQueue {
    private static final Queue<UploadTask> queue = new ArrayDeque<UploadTask>();
    private static long generation;
    private static int lastDrained;
    private static int dropped;
    private static int failed;
    private static int staleDropped;

    private PotassiumMeshUploadQueue() {}

    public static synchronized boolean offer(Runnable upload) {
        if (upload == null) return false;

        int max = Math.max(8, PotassiumConfig.maxMeshUploadsPerFrame * 8);
        if (queue.size() >= max) {
            dropped++;
            return false;
        }

        queue.offer(new UploadTask(upload, generation));
        return true;
    }

    public static void drainFrame() {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.meshUploadPipeline) {
            lastDrained = 0;
            return;
        }

        int configuredBudget = Math.max(1, PotassiumConfig.maxMeshUploadsPerFrame);
        int budget = MultiplayerPerformanceOptimizer.getMeshUploadBudget(configuredBudget);
        budget = WorldTransitionOptimizer.scaleMeshUploadBudget(budget);

        int drained = 0;

        while (drained < budget) {
            UploadTask task;
            long currentGeneration;

            synchronized (PotassiumMeshUploadQueue.class) {
                task = queue.poll();
                currentGeneration = generation;
            }

            if (task == null) break;

            if (task.generation != currentGeneration) {
                synchronized (PotassiumMeshUploadQueue.class) {
                    staleDropped++;
                }
                continue;
            }

            try {
                task.runnable.run();
            } catch (Throwable ignored) {
                synchronized (PotassiumMeshUploadQueue.class) {
                    failed++;
                }
                // Fail open: one optional upload must never stop the client.
            }
            drained++;
        }

        lastDrained = drained;
    }

    /**
     * Starts a new client world/render generation and invalidates queued
     * uploads produced for the previous world.
     */
    public static synchronized void beginWorldGeneration() {
        generation++;
        queue.clear();
        lastDrained = 0;
    }

    public static synchronized int size() {
        return queue.size();
    }

    public static synchronized int getLastDrained() {
        return lastDrained;
    }

    public static synchronized int getDropped() {
        return dropped;
    }

    public static synchronized int getFailed() {
        return failed;
    }

    public static synchronized int getStaleDropped() {
        return staleDropped;
    }

    public static synchronized void clear() {
        generation++;
        queue.clear();
        lastDrained = 0;
        dropped = 0;
        failed = 0;
        staleDropped = 0;
    }

    private static final class UploadTask {
        private final Runnable runnable;
        private final long generation;

        private UploadTask(Runnable runnable, long generation) {
            this.runnable = runnable;
            this.generation = generation;
        }
    }
}
