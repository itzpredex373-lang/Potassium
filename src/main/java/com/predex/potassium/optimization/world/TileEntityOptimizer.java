package com.predex.potassium.optimization.world;

import net.minecraft.tileentity.TileEntity;

public final class TileEntityOptimizer {
    private TileEntityOptimizer() {}

    public static boolean shouldProcess(TileEntity tile) {
        if (tile == null || tile.isInvalid()) return false;
        return tile.getWorld() != null;
    }
}