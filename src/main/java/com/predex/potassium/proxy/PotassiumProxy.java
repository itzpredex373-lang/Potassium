package com.predex.potassium.proxy;

/**
 * Common proxy. Client-only classes are kept out of the common bootstrap path.
 */
public class PotassiumProxy {
    public void registerCommonHooks() {
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(
                com.predex.potassium.pet.PotassiumPetManager.class);
    }

    public void registerClientHooks() {
        // No-op on dedicated servers.
    }
}
