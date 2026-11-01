package com.predex.potassium.proxy;

import com.predex.potassium.command.PotassiumCommand;
import com.predex.potassium.optimization.ParticleBurstLimiter;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

/** Client-only registration kept out of the dedicated-server class path. */
public final class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new ParticleBurstLimiter());
        ClientCommandHandler.instance.registerCommand(new PotassiumCommand());
    }
}
