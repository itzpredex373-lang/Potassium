package com.predex.potassium.client;

import com.mojang.authlib.GameProfile;
import com.predex.potassium.config.PotassiumConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Client-only Lunar-style mini pet system.
 *
 * Pets are never spawned into the world and never sent to a server.
 * One reusable client-side entity is rendered beside the local player.
 */
public final class PotassiumMiniPet {
    private static final double FOLLOW_DISTANCE = 1.25D;
    private static final double FOLLOW_SIDE = 0.72D;
    private static final double FOLLOW_HEIGHT = 0.10D;
    private static final double SNAP_DISTANCE = 8.0D;

    private Entity pet;
    private String activeType;
    private double petX;
    private double petY;
    private double petZ;
    private boolean initialized;
    private long lastWorldIdentity;

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        // The pet follows the player across dimensions/worlds.
        // The old client render entity is replaced automatically when the
        // world identity changes; the selected pet/config is preserved.
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        // Do not clear pet settings. The next world will recreate the local
        // render entity automatically from the saved selection.
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!PotassiumConfig.miniPetEnabled) return;

        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.thePlayer;
        if (player == null || minecraft.theWorld == null) return;

        ensurePet(minecraft, player);

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
            // Stable smoothing: no per-frame allocation and no tick dependency.
            double smoothing = 0.16D;
            petX += dx * smoothing;
            petY += dy * smoothing;
            petZ += dz * smoothing;
        }

        double idleBob = Math.sin(System.nanoTime() / 100000000.0D) * 0.025D;
        pet.setPositionAndRotation(petX, petY + idleBob, petZ, yaw, 0.0F);

        RenderManager renderManager = minecraft.getRenderManager();
        double renderX = petX - renderManager.renderPosX;
        double renderY = petY + idleBob - renderManager.renderPosY;
        double renderZ = petZ - renderManager.renderPosZ;

        float scale = Math.max(0.20F, Math.min(0.70F,
                PotassiumConfig.miniPetScale / 100.0F));

        GlStateManager.pushMatrix();
        try {
            GlStateManager.scale(scale, scale, scale);
            // 1.8.9's renderEntityWithPosYaw is the correct positioned render path.
            renderManager.renderEntityWithPosYaw(
                    pet,
                    renderX / scale,
                    renderY / scale,
                    renderZ / scale,
                    yaw,
                    partialTicks
            );
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private void ensurePet(Minecraft minecraft, EntityPlayerSP player) {
        long worldIdentity = System.identityHashCode(minecraft.theWorld);
        String requested = normalizeType(PotassiumConfig.miniPetType);

        if (pet == null
                || lastWorldIdentity != worldIdentity
                || !requested.equals(activeType)) {
            pet = createPet(minecraft, player, requested);
            activeType = requested;
            initialized = false;
            lastWorldIdentity = worldIdentity;
        }
    }

    private Entity createPet(Minecraft minecraft, EntityPlayerSP player, String type) {
        if ("predex".equals(type)) {
            GameProfile profile = player.getGameProfile();
            EntityOtherPlayerMP character = new EntityOtherPlayerMP(minecraft.theWorld, profile);
            character.noClip = true;
            character.setInvisible(false);
            return character;
        }

        if ("dragon".equals(type)) {
            EntityDragon dragon = new EntityDragon(minecraft.theWorld);
            dragon.noClip = true;
            return dragon;
        }

        if ("devil".equals(type)) {
            EntityMagmaCube devil = new EntityMagmaCube(minecraft.theWorld);
            devil.setSlimeSize(2);
            devil.noClip = true;
            return devil;
        }

        if ("blaze".equals(type)) {
            EntityBlaze blaze = new EntityBlaze(minecraft.theWorld);
            blaze.noClip = true;
            return blaze;
        }

        if ("slime".equals(type)) {
            EntitySlime slime = new EntitySlime(minecraft.theWorld);
            slime.setSlimeSize(2);
            slime.noClip = true;
            return slime;
        }

        if ("endermite".equals(type)) {
            EntityEndermite endermite = new EntityEndermite(minecraft.theWorld);
            endermite.noClip = true;
            return endermite;
        }

        if ("bat".equals(type)) {
            EntityBat bat = new EntityBat(minecraft.theWorld);
            bat.noClip = true;
            return bat;
        }

        if ("chicken".equals(type)) {
            EntityChicken chicken = new EntityChicken(minecraft.theWorld);
            chicken.noClip = true;
            return chicken;
        }

        if ("rabbit".equals(type)) {
            EntityRabbit rabbit = new EntityRabbit(minecraft.theWorld);
            rabbit.noClip = true;
            return rabbit;
        }

        if ("ocelot".equals(type)) {
            EntityOcelot ocelot = new EntityOcelot(minecraft.theWorld);
            ocelot.noClip = true;
            return ocelot;
        }

        EntityWolf wolf = new EntityWolf(minecraft.theWorld);
        wolf.setTamed(true);
        wolf.noClip = true;
        return wolf;
    }

    private String normalizeType(String type) {
        if ("predex".equals(type)
                || "dragon".equals(type)
                || "devil".equals(type)
                || "blaze".equals(type)
                || "slime".equals(type)
                || "endermite".equals(type)
                || "bat".equals(type)
                || "chicken".equals(type)
                || "rabbit".equals(type)
                || "ocelot".equals(type)
                || "wolf".equals(type)) {
            return type;
        }
        return "predex";
    }

    private void reset() {
        pet = null;
        activeType = null;
        initialized = false;
        lastWorldIdentity = 0L;
    }
}
