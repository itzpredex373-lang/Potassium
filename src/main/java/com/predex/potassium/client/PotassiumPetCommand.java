package com.predex.potassium.client;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public final class PotassiumPetCommand extends CommandBase {
    @Override public String getCommandName() { return "pet"; }
    @Override public String getCommandUsage(ICommandSender sender) { return "/pet"; }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        // The menu itself is always accessible. The pet render/toggle remains
        // profile-aware, so /pet never becomes a dead command on High/Medium.
        PotassiumPetMenuScreen.open();
    }

    @Override public int getRequiredPermissionLevel() { return 0; }
    @Override public boolean canCommandSenderUseCommand(ICommandSender sender) { return true; }

    @Override
    public java.util.List<String> addTabCompletionOptions(
            ICommandSender sender, String[] args, BlockPos pos) {
        return java.util.Collections.emptyList();
    }
}
