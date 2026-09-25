package com.predex.potassium.pet;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public final class PotassiumPetManager {
    private PotassiumPetManager() {}

    public static void apply(EntityPlayer player, String type, boolean enabled) {
        if (player == null || player.worldObj == null || player.worldObj.isRemote) return;

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
