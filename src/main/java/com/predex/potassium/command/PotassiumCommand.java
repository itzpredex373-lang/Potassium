package com.predex.potassium.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.predex.potassium.optimization.ParticleBurstLimiter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Read-only diagnostics with an optional local text report. */
@SideOnly(Side.CLIENT)
public final class PotassiumCommand extends CommandBase {
    @Override
    public String getCommandName() { return "potassium"; }

    @Override
    public String getCommandUsage(ICommandSender sender) { return "/potassium [status|report]"; }

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
        sender.addChatMessage(new ChatComponentText("§7OS: §f" + System.getProperty("os.name", "unknown")
                + " (" + System.getProperty("os.arch", "unknown") + ")"));
        sender.addChatMessage(new ChatComponentText("§7CPU threads: §f" + runtime.availableProcessors()));
        sender.addChatMessage(new ChatComponentText("§7Memory: §f" + usedMb + " MB used / "
                + allocatedMb + " MB allocated / " + maxMb + " MB max"));
        sender.addChatMessage(new ChatComponentText("§7OptiFine: §f" + detectOptiFine()));
        sender.addChatMessage(new ChatComponentText("§7Optimization: §eParticle burst limiter (max "
                + ParticleBurstLimiter.MAX_PARTICLES_PER_TICK + " new particles/client tick)"));
        sender.addChatMessage(new ChatComponentText("§8Existing particles and entity rendering are unchanged."));
    }

    private void writeReport(ICommandSender sender) {
        final Minecraft minecraft = Minecraft.getMinecraft();
        File report = new File(minecraft.mcDataDir, "potassium-diagnostics.txt");
        Runtime runtime = Runtime.getRuntime();
        StringBuilder contents = new StringBuilder();
        contents.append("Potassium Diagnostics").append(System.lineSeparator());
        contents.append("Generated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.ROOT)
                .format(new Date())).append(System.lineSeparator());
        contents.append("Minecraft: ").append(minecraft.getVersion()).append(System.lineSeparator());
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
        contents.append("Optimization: client particle burst admission cap ")
                .append(ParticleBurstLimiter.MAX_PARTICLES_PER_TICK).append(" per tick").append(System.lineSeparator());
        contents.append("Existing particles and entity rendering are unchanged.").append(System.lineSeparator());
        contents.append("No measured FPS improvement is claimed.").append(System.lineSeparator());

        if (!DiagnosticsWriter.submit(report, contents.toString(), sender, minecraft)) {
            sender.addChatMessage(new ChatComponentText(
                    "§ePotassium: Diagnostics writer is busy; try /potassium report again shortly."));
        } else {
            sender.addChatMessage(new ChatComponentText("§7Potassium: Saving diagnostic report in the background..."));
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
    public int getRequiredPermissionLevel() { return 0; }
}
