package com.predex.potassium.optimization.chunks;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Bounded worker-side mesh preparation queue.
 *
 * The queue intentionally accepts CPU-only preparation tasks. Minecraft
 * World/RenderChunk/GL access must stay on their owning threads. This keeps
 * the architecture safe while allowing the custom mesh backend to be added
 * without recreating vanilla's worker-thread races.
 */
public final class PotassiumChunkMeshBuildQueue {
    private static final int CAPACITY = 64;
    private static final BlockingQueue<Runnable> queue =
            new LinkedBlockingQueue<Runnable>(CAPACITY);
    private static final AtomicBoolean started = new AtomicBoolean();
    private static final AtomicInteger dropped = new AtomicInteger();

    private static final Thread[] workers = new Thread[2];

    private PotassiumChunkMeshBuildQueue() {}

    public static void start() {
        if (!PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.customMeshPreparation) return;
        if (!started.compareAndSet(false, true)) return;

        for (int i = 0; i < workers.length; i++) {
            final int index = i;
            workers[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    workerLoop(index);
                }
            }, "Potassium-Mesh-" + (i + 1));
            workers[i].setDaemon(true);
            workers[i].start();
        }
    }

    public static boolean offer(Runnable task) {
        if (task == null || !PerformanceManager.isOptimizationEnabled()
                || !PotassiumConfig.customMeshPreparation) return false;
        start();
        if (!queue.offer(task)) {
            dropped.incrementAndGet();
            return false;
        }
        return true;
    }

    private static void workerLoop(int index) {
        while (started.get()) {
            try {
                Runnable task = queue.take();
                try {
                    task.run();
                } catch (Throwable ignored) {
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            } catch (Throwable ignored) {
            }
        }
    }

    public static int size() { return queue.size(); }
    public static int getDropped() { return dropped.get(); }

    public static void clear() {
        queue.clear();
        dropped.set(0);
    }
}
