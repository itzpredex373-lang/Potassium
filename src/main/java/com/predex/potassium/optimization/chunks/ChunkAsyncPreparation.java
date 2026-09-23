package com.predex.potassium.optimization.chunks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Optional preparation executor. It performs only caller-supplied pure work;
 * Minecraft world/render objects must remain on the client thread.
 */
public final class ChunkAsyncPreparation {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> pending;

    public synchronized boolean submit(Runnable preparation) {
        if (preparation == null || (pending != null && !pending.isDone())) {
            return false;
        }
        pending = executor.submit(preparation);
        return true;
    }

    public synchronized boolean isBusy() {
        return pending != null && !pending.isDone();
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}