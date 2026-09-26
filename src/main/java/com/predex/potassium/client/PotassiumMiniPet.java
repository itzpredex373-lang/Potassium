package com.predex.potassium.client;

import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Pure client-side cosmetic pet system.
 *
 * The base Potassium build contains 20 original procedural companion models.
 * Pets are never registered as Forge entities, never sent to a server and
 * never participate in world AI/ticking.
 */
public final class PotassiumMiniPet {
    private static final double FOLLOW_DISTANCE = 1.25D;
    private static final double FOLLOW_SIDE = 0.72D;
    private static final double FOLLOW_HEIGHT = 0.08D;
    private static final double SNAP_DISTANCE = 8.0D;

    private double petX;
    private double petY;
    private double petZ;
    private boolean initialized;
    private long lastWorldIdentity;
    private String activeType;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!PerformanceProfileManager.isPetAllowed() || !PotassiumConfig.miniPetEnabled) return;

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null) return;

        String requested = normalizeType(PotassiumConfig.miniPetType);
        if (!requested.equals(activeType)) {
            activeType = requested;
            initialized = false;
        }

        long worldIdentity = System.identityHashCode(minecraft.theWorld);
        if (worldIdentity != lastWorldIdentity) {
            lastWorldIdentity = worldIdentity;
            initialized = false;
        }

        float partialTicks = event.partialTicks;
        double playerX = player.lastTickPosX
                + (player.posX - player.lastTickPosX) * partialTicks;
        double playerY = player.lastTickPosY
                + (player.posY - player.lastTickPosY) * partialTicks;
        double playerZ = player.lastTickPosZ
                + (player.posZ - player.lastTickPosZ) * partialTicks;

        float yaw = player.prevRotationYaw
                + (player.rotationYaw - player.prevRotationYaw) * partialTicks;

        float radians = yaw * 0.017453292F;
        double forwardX = -MathHelper.sin(radians);
        double forwardZ = MathHelper.cos(radians);
        double rightX = MathHelper.cos(radians);
        double rightZ = MathHelper.sin(radians);

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
            double smoothing = 0.18D;
            petX += dx * smoothing;
            petY += dy * smoothing;
            petZ += dz * smoothing;
        }

        // Tick-based phase keeps animation deterministic without a per-frame clock query.
        double animationTime = (player.ticksExisted + partialTicks) * 0.12D;
        double idleBob = Math.sin(animationTime) * 0.025D;

        RenderManager renderManager = minecraft.getRenderManager();
        double renderX = petX - renderManager.viewerPosX;
        double renderY = petY + idleBob - renderManager.viewerPosY;
        double renderZ = petZ - renderManager.viewerPosZ;

        float modelScale = 0.90F * Math.max(0.70F, Math.min(1.25F,
                PotassiumConfig.miniPetScale / 100.0F));

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(renderX, renderY + 0.10D, renderZ);
            GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
            PotassiumCustomPetModels.render(
                    activeType,
                    player.ticksExisted + partialTicks,
                    modelScale
            );
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private String normalizeType(String type) {
        return PotassiumPetTypes.get(PotassiumPetTypes.indexOf(type));
    }
}
