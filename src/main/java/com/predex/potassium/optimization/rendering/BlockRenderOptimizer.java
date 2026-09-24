package com.predex.potassium.optimization.rendering;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.PerformanceManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public final class BlockRenderOptimizer {
    private BlockRenderOptimizer() {}

    public static boolean isChunkInRenderRadius(
            int chunkX, int chunkZ,
            int playerChunkX, int playerChunkZ,
            int renderDistanceChunks) {
        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        int radius = Math.max(2, renderDistanceChunks);
        return dx * dx + dz * dz <= radius * radius;
    }

    public static boolean isChunkCenterInDistance(
            int chunkX, int chunkZ,
            double playerX, double playerZ,
            double maxDistance) {
        double centerX = chunkX * 16.0D + 8.0D;
        double centerZ = chunkZ * 16.0D + 8.0D;
        double dx = centerX - playerX;
        double dz = centerZ - playerZ;
        return dx * dx + dz * dz <= maxDistance * maxDistance;
    }

    public static int getChunkDistanceSq(
            int chunkX, int chunkZ,
            int playerChunkX, int playerChunkZ) {
        int dx = chunkX - playerChunkX;
        int dz = chunkZ - playerChunkZ;
        return dx * dx + dz * dz;
    }

    public static boolean shouldRenderFace(IBlockAccess world, IBlockState state,
                                            BlockPos pos, EnumFacing side) {
        if (world == null || state == null || pos == null || side == null) return true;
        return state.getBlock().shouldSideBeRendered(world, pos, side);
    }

    public static boolean isFullyOccluded(IBlockAccess world, IBlockState state, BlockPos pos) {
        if (world == null || state == null || pos == null) return false;
        if (!state.getBlock().isOpaqueCube() || !state.getBlock().isFullCube()) return false;

        for (EnumFacing side : EnumFacing.values()) {
            IBlockState neighbor = world.getBlockState(pos.offset(side));
            if (neighbor == null || !neighbor.getBlock().isOpaqueCube()
                    || !neighbor.getBlock().isFullCube()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isFaceCullingEnabled() {
        return PerformanceManager.isOptimizationEnabled() && PotassiumConfig.blockFaceCulling;
    }
}