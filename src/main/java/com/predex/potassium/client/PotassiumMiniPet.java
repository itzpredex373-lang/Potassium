package com.predex.potassium.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;

/**
 * Client-only Lunar-style mini pet.
 *
 * The pet is never spawned into the world/server. It is a single reusable
 * client-side render entity that smoothly follows the local player.
 */
public final class PotassiumMiniPet {
    private static final double FOLLOW_DISTANCE = 1.25D;
    private static final double FOLLOW_SIDE = 0.72D;
    private static final double FOLLOW_HEIGHT = 0.10D;
    private static final double SNAP_DISTANCE = 8.0D;
    private static final float PET_SCALE_BASE = 0.01F;

    private EntityWolf pet;
    private double petX;
    private double petY;
    private double petZ;
    private boolean initialized;
    private long lastWorldIdentity;

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world != null && event.world.isRemote) {
            reset();
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.world != null && event.world.isRemote) {
            reset();
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!com.predex.potassium.config.PotassiumConfig.miniPetEnabled) {
            return;
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null) {
            return;
        }

        ensurePet(minecraft);

        float partialTicks = event.partialTicks;
        double playerX = player.lastTickPosX
                + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double playerY = player.lastTickPosY
                + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double playerZ = player.lastTickPosZ
                + (player.posZ - player.lastTickPosZ) * (double) partialTicks;

        float yaw = player.prevRotationYaw
                + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

        double radians = Math.toRadians(yaw);
        double forwardX = -MathHelper.sin((float) radians);
        double forwardZ = MathHelper.cos((float) radians);
        double rightX = MathHelper.cos((float) radians);
        double rightZ = MathHelper.sin((float) radians);

        // Behind + slightly to the player's right, like a small companion.
        double targetX = playerX - forwardX * FOLLOW_DISTANCE + rightX * FOLLOW_SIDE;
        double targetY = playerY + FOLLOW_HEIGHT;
        double targetZ = playerZ - forwardZ * FOLLOW_DISTANCE + rightZ * FOLLOW_SIDE;

        if (!initialized) {
            petX = targetX;
            petY = targetY;
            petZ = targetZ;
            initialized = true;
        }

        double dx = targetX - petX;
        double dy = targetY - petY;
        double dz = targetZ - petZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;

        if (distanceSq > SNAP_DISTANCE * SNAP_DISTANCE) {
            petX = targetX;
            petY = targetY;
            petZ = targetZ;
        } else {
            // Exponential-looking smoothing that remains stable at variable FPS.
            double smoothing = 1.0D - Math.pow(0.001D, Math.max(0.0D, partialTicks + 0.5D));
            smoothing = Math.min(0.22D, Math.max(0.08D, smoothing));
            petX += dx * smoothing;
            petY += dy * smoothing;
            petZ += dz * smoothing;
        }

        double idleBob = Math.sin((System.nanoTime() / 100000000.0D)) * 0.025D;
        pet.setPositionAndRotation(
                petX,
                petY + idleBob,
                petZ,
                yaw,
                0.0F
        );

        RenderManager renderManager = minecraft.getRenderManager();
        double renderX = petX - renderManager.renderPosX;
        double renderY = petY + idleBob - renderManager.renderPosY;
        double renderZ = petZ - renderManager.renderPosZ;
        float petScale = Math.max(0.25F, Math.min(0.75F,
                com.predex.potassium.config.PotassiumConfig.miniPetScale * PET_SCALE_BASE));

        GlStateManager.pushMatrix();
        try {
            GlStateManager.scale(petScale, petScale, petScale);
            renderManager.renderEntityStatic(
                    pet,
                    renderX / PET_SCALE,
                    renderY / PET_SCALE,
                    renderZ / PET_SCALE,
                    yaw,
                    partialTicks,
                    false
            );
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private void ensurePet(Minecraft minecraft) {
        long worldIdentity = System.identityHashCode(minecraft.theWorld);
        if (pet == null || lastWorldIdentity != worldIdentity) {
            pet = new EntityWolf(minecraft.theWorld);
            pet.ignoreFrustumCheck = true;
            initialized = false;
            lastWorldIdentity = worldIdentity;
        }
    }

    private void reset() {
        pet = null;
        initialized = false;
        lastWorldIdentity = 0L;
    }
}
