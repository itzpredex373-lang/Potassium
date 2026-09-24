package com.predex.potassium.dev;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public final class PotassiumDevCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "dev";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/dev";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().displayGuiScreen(new PotassiumDevScreen());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
