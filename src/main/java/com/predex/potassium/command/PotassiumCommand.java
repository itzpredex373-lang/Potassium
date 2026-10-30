package com.predex.potassium.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Read-only diagnostic command. It does not change game settings,
 * trigger garbage collection, or display a permanent HUD.
 */
@SideOnly(Side.CLIENT)
public final class PotassiumCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "potassium";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/potassium [status]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1 || (args.length == 1 && !"status".equalsIgnoreCase(args[0]))) {
            sender.addChatMessage(new ChatComponentText("§bPotassium§7: Usage: /potassium status"));
            return;
        }

        Runtime runtime = Runtime.getRuntime();
        long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L;
        long allocatedMb = runtime.totalMemory() / 1048576L;
        long maxMb = runtime.maxMemory() / 1048576L;

        sender.addChatMessage(new ChatComponentText("§b§lPotassium Diagnostics"));
        sender.addChatMessage(new ChatComponentText("§7Minecraft: §f" + Minecraft.getMinecraft().getVersion()));
        sender.addChatMessage(new ChatComponentText("§7Java: §f" + System.getProperty("java.version")));
        sender.addChatMessage(new ChatComponentText("§7Memory: §f" + usedMb + " MB used / "
                + allocatedMb + " MB allocated / " + maxMb + " MB max"));
        sender.addChatMessage(new ChatComponentText("§7OptiFine: §f" + detectOptiFine()));
        sender.addChatMessage(new ChatComponentText("§7Optimization modules: §eNone installed yet"));
        sender.addChatMessage(new ChatComponentText("§8No FPS gain is claimed by this diagnostic build."));
    }

    private String detectOptiFine() {
        try {
            Class.forName("Config", false, PotassiumCommand.class.getClassLoader());
            return "Detected";
        } catch (ClassNotFoundException | LinkageError | SecurityException ignored) {
            return "Not detected";
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
