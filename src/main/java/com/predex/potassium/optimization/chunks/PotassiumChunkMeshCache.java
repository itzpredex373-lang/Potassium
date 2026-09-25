package com.predex.potassium.optimization.chunks;

import com.predex.potassium.optimization.PerformanceManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Lightweight compile-state cache used by the custom chunk pipeline.
 *
 * It deliberately stores only lifecycle state, not Minecraft world objects
 * or GL handles. This keeps the cache safe around the vanilla worker/GL
 * boundary while allowing Potassium to coalesce repeated rebuild requests.
 */
public final class PotassiumChunkMeshCache {
    public enum State {
        CLEAN,
        QUEUED,
        BUILDING,
        UPLOAD_PENDING
    }

    private static final Map<RenderChunk, Entry> entries =
            Collections.synchronizedMap(new IdentityHashMap<RenderChunk, Entry>());

    private PotassiumChunkMeshCache() {}

    public static boolean markQueued(RenderChunk chunk) {
        if (!PerformanceManager.isOptimizationEnabled() || chunk == null) return true;

        synchronized (entries) {
            Entry entry = entries.get(chunk);
            if (entry != null && (entry.state == State.QUEUED
                    || entry.state == State.BUILDING
                    || entry.state == State.UPLOAD_PENDING)) {
                return false;
            }

            Entry next = entry == null ? new Entry() : entry;
            next.state = State.QUEUED;
            next.position = safePosition(chunk);
            entries.put(chunk, next);
            return true;
        }
    }

    public static void markBuilding(RenderChunk chunk) {
        if (chunk == null) return;
        synchronized (entries) {
            Entry entry = entries.get(chunk);
            if (entry != null) entry.state = State.BUILDING;
        }
    }

    public static void markUploadPending(RenderChunk chunk) {
        if (chunk == null) return;
        synchronized (entries) {
            Entry entry = entries.get(chunk);
            if (entry != null) entry.state = State.UPLOAD_PENDING;
        }
    }

    public static void markClean(RenderChunk chunk) {
        if (chunk == null) return;
        synchronized (entries) {
            Entry entry = entries.get(chunk);
            if (entry != null) entry.state = State.CLEAN;
        }
    }

    public static void remove(RenderChunk chunk) {
        if (chunk == null) return;
        entries.remove(chunk);
    }

    public static State getState(RenderChunk chunk) {
        synchronized (entries) {
            Entry entry = entries.get(chunk);
            return entry == null ? State.CLEAN : entry.state;
        }
    }

    public static int size() {
        synchronized (entries) {
            return entries.size();
        }
    }

    private static BlockPos safePosition(RenderChunk chunk) {
        try {
            return chunk.getPosition();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static final class Entry {
        private State state = State.CLEAN;
        private BlockPos position;
    }
}
