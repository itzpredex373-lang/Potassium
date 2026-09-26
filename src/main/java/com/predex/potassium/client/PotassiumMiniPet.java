package com.predex.potassium.client;

import com.mojang.authlib.GameProfile;
import com.predex.potassium.config.PotassiumConfig;
import com.predex.potassium.optimization.profile.PerformanceProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Pure client-side cosmetic pet system.
 *
 * No pet entity is registered with Forge, no packet is sent, and no server
 * world state is changed. The selected pet exists only as a local render
 * object and is visible only to the local player.
 */
public final class PotassiumMiniPet {
    private static final double FOLLOW_DISTANCE = 1.25D;
    private static final double FOLLOW_SIDE = 0.72D;
    private static final double FOLLOW_HEIGHT = 0.08D;
    private static final double SNAP_DISTANCE = 8.0D;

    private Entity pet;
    private String activeType;
    private double petX;
    private double petY;
    private double petZ;
    private boolean initialized;
    private long lastWorldIdentity;

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!PerformanceProfileManager.isPetAllowed() || !PotassiumConfig.miniPetEnabled) return;

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
            double smoothing = 0.18D;
            petX += dx * smoothing;
            petY += dy * smoothing;
            petZ += dz * smoothing;
        }

        double idleBob = Math.sin(System.nanoTime() / 100000000.0D) * 0.025D;
        pet.setPositionAndRotation(petX, petY + idleBob, petZ, yaw, 0.0F);

        RenderManager renderManager = minecraft.getRenderManager();
        double renderX = petX - renderManager.viewerPosX;
        double renderY = petY + idleBob - renderManager.viewerPosY;
        double renderZ = petZ - renderManager.viewerPosZ;

        float scale = getScale(activeType)
                * Math.max(0.25F, Math.min(0.65F,
                PotassiumConfig.miniPetScale / 100.0F));

        if (PotassiumCustomPetModels.isCustom(activeType)) {
            GlStateManager.pushMatrix();
            try {
                GlStateManager.translate(renderX, renderY + 0.10D, renderZ);
                GlStateManager.rotate(-yaw, 0.0F, 1.0F, 0.0F);
                PotassiumCustomPetModels.render(
                        activeType,
                        player.ticksExisted + partialTicks,
                        0.90F * Math.max(0.70F, Math.min(1.25F,
                                PotassiumConfig.miniPetScale / 100.0F))
                );
            } finally {
                GlStateManager.popMatrix();
            }
        } else {
            GlStateManager.pushMatrix();
            try {
                GlStateManager.scale(scale, scale, scale);
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
        if ("predex".equals(type) || "mini_me".equals(type)
                || "astronaut".equals(type)) {
            GameProfile profile = player.getGameProfile();
            EntityOtherPlayerMP character =
                    new EntityOtherPlayerMP(minecraft.theWorld, profile);
            character.noClip = true;
            return character;
        }

        if ("king_dragon".equals(type) || "black_dragon".equals(type) || "mini_ender_dragon".equals(type)) {
            EntityDragon dragon = new EntityDragon(minecraft.theWorld);
            dragon.noClip = true;
            return dragon;
        }

        if ("devil".equals(type) || "slime".equals(type) || "slime_king".equals(type) || "dragon_egg".equals(type)) {
            EntityMagmaCube cube = new EntityMagmaCube(minecraft.theWorld);
            cube.noClip = true;
            return cube;
        }

        if ("shadow_dragon".equals(type) || "voidling".equals(type) || "void_orb".equals(type) || "tiny_reaper".equals(type)) {
            EntityEndermite endermite = new EntityEndermite(minecraft.theWorld);
            endermite.noClip = true;
            return endermite;
        }

        if ("wyvern".equals(type) || "butterfly".equals(type)
                || "bee".equals(type) || "spirit".equals(type)
                || "ghost".equals(type) || "mini_phoenix".equals(type) || "crystal_fairy".equals(type)) {
            EntityBat bat = new EntityBat(minecraft.theWorld);
            bat.noClip = true;
            return bat;
        }

        if ("robot".equals(type) || "pixel_robot".equals(type) || "tiny_knight".equals(type) || "mini_golem".equals(type)) {
            EntityIronGolem golem = new EntityIronGolem(minecraft.theWorld);
            golem.noClip = true;
            return golem;
        }

        if ("fox".equals(type) || "kitsune".equals(type)
                || "cat".equals(type) || "heart_cat".equals(type) || "shadow_fox".equals(type) || "cyber_cat".equals(type) || "moon_cat".equals(type)) {
            EntityOcelot cat = new EntityOcelot(minecraft.theWorld);
            cat.noClip = true;
            return cat;
        }

        if ("wolf".equals(type) || "dog".equals(type) || "mini_astral_wolf".equals(type)) {
            EntityWolf wolf = new EntityWolf(minecraft.theWorld);
            wolf.setTamed(true);
            wolf.noClip = true;
            return wolf;
        }

        if ("bunny".equals(type) || "moon_rabbit".equals(type) || "ghost_bunny".equals(type)) {
            EntityRabbit rabbit = new EntityRabbit(minecraft.theWorld);
            rabbit.noClip = true;
            return rabbit;
        }

        if ("dino".equals(type) || "crow".equals(type)) {
            EntityChicken chicken = new EntityChicken(minecraft.theWorld);
            chicken.noClip = true;
            return chicken;
        }

        if ("capybara".equals(type)) {
            EntityPig pig = new EntityPig(minecraft.theWorld);
            pig.noClip = true;
            return pig;
        }

        if ("stag".equals(type)) {
            EntityHorse horse = new EntityHorse(minecraft.theWorld);
            horse.noClip = true;
            return horse;
        }

        if ("inferno".equals(type)) {
            EntityBlaze blaze = new EntityBlaze(minecraft.theWorld);
            blaze.noClip = true;
            return blaze;
        }

        if ("ender".equals(type)) {
            EntityEndermite ender = new EntityEndermite(minecraft.theWorld);
            ender.noClip = true;
            return ender;
        }

        if ("guardian".equals(type)) {
            EntityGuardian guardian = new EntityGuardian(minecraft.theWorld);
            guardian.noClip = true;
            return guardian;
        }

        EntityWolf fallback = new EntityWolf(minecraft.theWorld);
        fallback.setTamed(true);
        fallback.noClip = true;
        return fallback;
    }

    private float getScale(String type) {
        if ("king_dragon".equals(type) || "black_dragon".equals(type) || "mini_ender_dragon".equals(type)) return 0.42F;
        if ("robot".equals(type) || "guardian".equals(type) || "pixel_robot".equals(type) || "mini_golem".equals(type) || "tiny_knight".equals(type)) return 0.48F;
        if ("stag".equals(type)) return 0.45F;
        if ("inferno".equals(type)) return 0.55F;
        if ("wyvern".equals(type) || "butterfly".equals(type)
                || "bee".equals(type) || "spirit".equals(type)
                || "ghost".equals(type) || "mini_phoenix".equals(type) || "crystal_fairy".equals(type)) return 0.70F;
        if ("bunny".equals(type) || "moon_rabbit".equals(type) || "ghost_bunny".equals(type)) return 0.85F;
        return 0.65F;
    }

    private String normalizeType(String type) {
        return PotassiumPetTypes.get(PotassiumPetTypes.indexOf(type));
    }
}
