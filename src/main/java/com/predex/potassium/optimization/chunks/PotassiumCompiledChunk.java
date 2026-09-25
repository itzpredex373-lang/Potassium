package com.predex.potassium.optimization.chunks;

import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.util.EnumWorldBlockLayer;

/**
 * Small visibility bridge for Minecraft 1.8.9's protected CompiledChunk
 * layer-used marker. No rendering state is changed here.
 */
public final class PotassiumCompiledChunk extends CompiledChunk {
    public void markLayerUsed(EnumWorldBlockLayer layer) {
        setLayerUsed(layer);
    }
}
