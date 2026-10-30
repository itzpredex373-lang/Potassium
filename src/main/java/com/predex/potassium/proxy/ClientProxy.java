package com.predex.potassium.proxy;

import com.predex.potassium.command.PotassiumCommand;
import net.minecraftforge.client.ClientCommandHandler;

/** Client-only registration kept out of the dedicated-server class path. */
public final class ClientProxy extends CommonProxy {
    @Override
    public void init() {
        ClientCommandHandler.instance.registerCommand(new PotassiumCommand());
    }
}
