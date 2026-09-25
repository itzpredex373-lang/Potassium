package com.predex.potassium.pet;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.UUID;

public final class EntityPotassiumPet extends EntityCreature {
    private static final int WATCHER_TYPE = 20;
    private static final int WATCHER_OWNER = 21;

    public EntityPotassiumPet(World world) {
        super(world);
        setSize(0.55F, 0.65F);
        noClip = true;
        setNoAI(true);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataWatcher.addObject(WATCHER_TYPE, Byte.valueOf((byte) 0));
        dataWatcher.addObject(WATCHER_OWNER, "");
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.0D);
    }

    public void setPetType(String type) {
        dataWatcher.updateObject(WATCHER_TYPE,
                Byte.valueOf((byte) PotassiumPetTypes.indexOf(type)));
    }

    public String getPetType() {
        return PotassiumPetTypes.get(
                dataWatcher.getWatchableObjectByte(WATCHER_TYPE) & 0xFF);
    }

    public void setOwnerUuid(UUID uuid) {
        dataWatcher.updateObject(WATCHER_OWNER,
                uuid == null ? "" : uuid.toString());
    }

    public UUID getOwnerUuid() {
        String value = dataWatcher.getWatchableObjectString(WATCHER_OWNER);
        if (value == null || value.length() == 0) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (worldObj.isRemote) return;

        UUID ownerUuid = getOwnerUuid();
        if (ownerUuid == null) {
            setDead();
            return;
        }

        EntityPlayer owner = worldObj.getPlayerEntityByUUID(ownerUuid);
        if (owner == null || owner.isDead) {
            setDead();
            return;
        }

        float yaw = owner.rotationYaw * 0.017453292F;
        double forwardX = -MathHelper.sin(yaw);
        double forwardZ = MathHelper.cos(yaw);
        double rightX = MathHelper.cos(yaw);
        double rightZ = MathHelper.sin(yaw);

        double targetX = owner.posX - forwardX * 1.20D + rightX * 0.72D;
        double targetY = owner.posY + 0.05D;
        double targetZ = owner.posZ - forwardZ * 1.20D + rightZ * 0.72D;

        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double distanceSq = dx * dx + dy * dy + dz * dz;

        if (distanceSq > 64.0D) {
            setPosition(targetX, targetY, targetZ);
        } else {
            setPosition(
                    posX + dx * 0.28D,
                    posY + dy * 0.28D,
                    posZ + dz * 0.28D);
        }

        rotationYaw = owner.rotationYaw;
        rotationYawHead = rotationYaw;
        prevRotationYaw = rotationYaw;
        prevRotationYawHead = rotationYawHead;
        motionX = motionY = motionZ = 0.0D;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean attackEntityFrom(net.minecraft.util.DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setString("PetType", getPetType());
        UUID owner = getOwnerUuid();
        if (owner != null) tag.setString("OwnerUUID", owner.toString());
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        setPetType(tag.getString("PetType"));
        String owner = tag.getString("OwnerUUID");
        if (owner != null && owner.length() > 0) {
            try {
                setOwnerUuid(UUID.fromString(owner));
            } catch (IllegalArgumentException ignored) {
                setDead();
            }
        }
    }
}
