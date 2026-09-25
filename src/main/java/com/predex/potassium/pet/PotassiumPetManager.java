package com.predex.potassium.pet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.List;
import java.util.UUID;

public final class PotassiumPetManager {
    private static final String NBT_ENABLED = "PotassiumPetEnabled";
    private static final String NBT_TYPE = "PotassiumPetType";

    private PotassiumPetManager() {}

    public static void apply(EntityPlayer player, String type, boolean enabled) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return;

        type = PotassiumPetTypes.get(PotassiumPetTypes.indexOf(type));

        NBTTagCompound data = player.getEntityData();
        data.setBoolean(NBT_ENABLED, enabled);
        data.setString(NBT_TYPE, type);

        EntityPotassiumPet existing = find(player);
        if (!enabled) {
            if (existing != null) existing.setDead();
            return;
        }

        if (existing == null) {
            existing = new EntityPotassiumPet(player.worldObj);
            existing.setOwnerUuid(player.getUniqueID());
            existing.setPosition(player.posX, player.posY + 0.05D, player.posZ);
            player.worldObj.spawnEntityInWorld(existing);
        }

        existing.setPetType(type);
    }

    @SubscribeEvent
    public static void onChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        restore(event.player);
    }

    @SubscribeEvent
    public static void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        restore(event.player);
    }

    private static void restore(EntityPlayer player) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return;

        NBTTagCompound data = player.getEntityData();
        boolean enabled = data.getBoolean(NBT_ENABLED);
        String type = data.getString(NBT_TYPE);

        if (enabled && type.length() > 0) {
            apply(player, type, true);
        }
    }

    public static EntityPotassiumPet find(EntityPlayer player) {
        if (player == null || player.worldObj == null) return null;
        UUID owner = player.getUniqueID();

        List<Entity> entities = player.worldObj.loadedEntityList;
        for (Entity entity : entities) {
            if (entity instanceof EntityPotassiumPet) {
                EntityPotassiumPet pet = (EntityPotassiumPet) entity;
                if (owner.equals(pet.getOwnerUuid())) return pet;
            }
        }
        return null;
    }
}
