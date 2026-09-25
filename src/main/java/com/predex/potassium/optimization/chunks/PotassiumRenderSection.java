package com.predex.potassium.optimization.chunks;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

/**
 * Lightweight render-section state used by Potassium's section pipeline.
 *
 * Minecraft 1.8.9 already owns the actual GL/VBO objects in RenderChunk.
 * This class deliberately keeps only CPU-side scheduling/visibility metadata
 * so the worker-thread and OpenGL-thread ownership rules remain intact.
 */
public final class PotassiumRenderSection {
    public static final int SIZE = 16;

    private final long key;
    private final int chunkX;
    private final int sectionY;
    private final int chunkZ;
    private long lastSeenFrame = -1L;
    private long lastBuiltFrame = -1L;
    private boolean dirty = true;
    private boolean visible;
    private boolean empty;
    private int visibleMask;

    public PotassiumRenderSection(int chunkX, int sectionY, int chunkZ) {
        this.chunkX = chunkX;
        this.sectionY = sectionY;
        this.chunkZ = chunkZ;
        this.key = key(chunkX, sectionY, chunkZ);
        // Newly observed sections are rendered until the next frustum refresh.
        this.visible = true;
    }

    public static long key(int chunkX, int sectionY, int chunkZ) {
        long x = chunkX & 0x3FFFFFL;
        long y = sectionY & 0xFFL;
        long z = chunkZ & 0x3FFFFFL;
        return (x << 26) | (y << 18) | z;
    }

    public AxisAlignedBB bounds() {
        int x = chunkX << 4;
        int y = sectionY << 4;
        int z = chunkZ << 4;
        return new AxisAlignedBB(x, y, z, x + SIZE, y + SIZE, z + SIZE);
    }

    public BlockPos origin() {
        return new BlockPos(chunkX << 4, sectionY << 4, chunkZ << 4);
    }

    public long getKey() { return key; }
    public int getChunkX() { return chunkX; }
    public int getSectionY() { return sectionY; }
    public int getChunkZ() { return chunkZ; }

    public long getLastSeenFrame() { return lastSeenFrame; }
    public void seen(long frame) { lastSeenFrame = frame; }

    public long getLastBuiltFrame() { return lastBuiltFrame; }
    public void built(long frame) {
        lastBuiltFrame = frame;
        dirty = false;
    }

    public boolean isDirty() { return dirty; }
    public void markDirty() { dirty = true; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isEmpty() { return empty; }
    public void setEmpty(boolean empty) { this.empty = empty; }

    public int getVisibleMask() { return visibleMask; }
    public void setVisibleMask(int visibleMask) { this.visibleMask = visibleMask; }
}
