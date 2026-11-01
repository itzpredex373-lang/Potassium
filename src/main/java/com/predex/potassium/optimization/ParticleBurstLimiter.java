package com.predex.potassium.optimization;

import net.minecraft.client.particle.EntityFX;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Bounds particle bursts admitted during a single client tick.
 *
 * This deliberately does not replace vanilla/OptiFine rendering or remove
 * already-existing particles. It only rejects excess newly-added visual
 * particles during unusually large bursts.
 */
public final class ParticleBurstLimiter {
    public static final int MAX_PARTICLES_PER_TICK = 256;

    private int admittedThisTick;
    private long rejectedTotal;

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            admittedThisTick = 0;
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.entity instanceof EntityFX) || event.world.isRemote == false) {
            return;
        }

        if (admittedThisTick >= MAX_PARTICLES_PER_TICK) {
            event.setCanceled(true);
            rejectedTotal++;
            return;
        }
        admittedThisTick++;
    }

    public int getAdmittedThisTick() {
        return admittedThisTick;
    }

    public long getRejectedTotal() {
        return rejectedTotal;
    }
}
