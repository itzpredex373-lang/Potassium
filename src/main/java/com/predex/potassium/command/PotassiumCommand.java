package com.predex.potassium.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Read-only diagnostics with an optional local text report.
 * No settings are changed and no telemetry is uploaded.
 */
@SideOnly(Side.CLIENT)
public final class PotassiumCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "potassium";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/potassium [status|report]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 1 || (args.length == 1
                && !"status".equalsIgnoreCase(args[0])
                && !"report".equalsIgnoreCase(args[0]))) {
            sender.addChatMessage(new ChatComponentText("§bPotassium§7: Usage: /potassium [status|report]"));
            return;
        }

        if (args.length == 1 && "report".equalsIgnoreCase(args[0])) {
            writeReport(sender);
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
        sender.addChatMessage(new ChatComponentText("§8This diagnostic build does not claim FPS gains."));
    }

    private void writeReport(ICommandSender sender) {
        File gameDir = Minecraft.getMinecraft().mcDataDir;
        File report = new File(gameDir, "potassium-diagnostics.txt");
        Runtime runtime = Runtime.getRuntime();

        StringBuilder contents = new StringBuilder();
        contents.append("Potassium Diagnostics").append(System.lineSeparator());
        contents.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.ROOT)
                .format(new Date())).append(System.lineSeparator());
        contents.append("Minecraft: ").append(Minecraft.getMinecraft().getVersion()).append(System.lineSeparator());
        contents.append("Java version: ").append(System.getProperty("java.version", "unknown")).append(System.lineSeparator());
        contents.append("Java VM: ").append(System.getProperty("java.vm.name", "unknown")).append(System.lineSeparator());
        contents.append("Operating system: ").append(System.getProperty("os.name", "unknown")).append(" ")
                .append(System.getProperty("os.version", "unknown")).append(System.lineSeparator());
        contents.append("Architecture: ").append(System.getProperty("os.arch", "unknown")).append(System.lineSeparator());
        contents.append("Available processors: ").append(runtime.availableProcessors()).append(System.lineSeparator());
        contents.append("Memory used MB: ").append((runtime.totalMemory() - runtime.freeMemory()) / 1048576L)
                .append(System.lineSeparator());
        contents.append("Memory allocated MB: ").append(runtime.totalMemory() / 1048576L).append(System.lineSeparator());
        contents.append("Memory maximum MB: ").append(runtime.maxMemory() / 1048576L).append(System.lineSeparator());
        contents.append("OptiFine detectable: ").append(detectOptiFine()).append(System.lineSeparator());
        contents.append("Optimization modules: none").append(System.lineSeparator());
        contents.append("No performance improvement is claimed by this diagnostic build.")
                .append(System.lineSeparator());

        try {
            File parent = report.getParentFile();
            if (parent != null && !parent.isDirectory() && !parent.mkdirs() && !parent.isDirectory()) {
                throw new IOException("Could not create the Minecraft game directory");
            }
            try (BufferedWriter writer = Files.newBufferedWriter(report.toPath(), StandardCharsets.UTF_8)) {
                writer.write(contents.toString());
            }
            sender.addChatMessage(new ChatComponentText("§aPotassium: Diagnostic report saved to "
                    + report.getName() + " in your Minecraft folder."));
        } catch (IOException exception) {
            sender.addChatMessage(new ChatComponentText("§cPotassium: Could not save diagnostic report: "
                    + exception.getMessage()));
        }
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
