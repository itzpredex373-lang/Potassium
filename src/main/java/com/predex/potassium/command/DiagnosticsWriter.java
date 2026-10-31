package com.predex.potassium.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/** Bounded background writer for local diagnostic reports. */
final class DiagnosticsWriter {
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            1, 1, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable task) {
                    Thread thread = new Thread(task, "Potassium-Diagnostics");
                    thread.setDaemon(true);
                    return thread;
                }
            }, new ThreadPoolExecutor.AbortPolicy());

    private DiagnosticsWriter() { }

    static boolean submit(final File destination, final String contents,
                          final ICommandSender sender, final Minecraft minecraft) {
        try {
            EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    File temporary = new File(destination.getParentFile(), destination.getName() + ".tmp");
                    try {
                        Files.write(temporary.toPath(), contents.getBytes(StandardCharsets.UTF_8));
                        try {
                            Files.move(temporary.toPath(), destination.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                        } catch (AtomicMoveNotSupportedException ignored) {
                            Files.move(temporary.toPath(), destination.toPath(),
                                    StandardCopyOption.REPLACE_EXISTING);
                        }
                        minecraft.addScheduledTask(new Runnable() {
                            @Override
                            public void run() {
                                sender.addChatMessage(new ChatComponentText("§aPotassium: Diagnostic report saved to "
                                        + destination.getName() + " in your Minecraft folder."));
                            }
                        });
                    } catch (final IOException exception) {
                        temporary.delete();
                        minecraft.addScheduledTask(new Runnable() {
                            @Override
                            public void run() {
                                sender.addChatMessage(new ChatComponentText("§cPotassium: Could not save diagnostic report: "
                                        + exception.getMessage()));
                            }
                        });
                    }
                }
            });
            return true;
        } catch (RejectedExecutionException busy) {
            return false;
        }
    }
}
