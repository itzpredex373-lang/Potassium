package com.predex.potassium.pet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PotassiumPetNetwork {
    public static final SimpleNetworkWrapper CHANNEL =
            NetworkRegistry.INSTANCE.newSimpleChannel("potassium_pet");

    private static boolean initialized;

    private PotassiumPetNetwork() {}

    public static void init() {
        if (initialized) return;
        CHANNEL.registerMessage(PetSelectionMessage.Handler.class,
                PetSelectionMessage.class, 0, Side.SERVER);
        initialized = true;
    }

    public static final class PetSelectionMessage implements IMessage {
        private int typeIndex;
        private boolean enabled;

        public PetSelectionMessage() {}

        public PetSelectionMessage(String type, boolean enabled) {
            this.typeIndex = PotassiumPetTypes.indexOf(type);
            this.enabled = enabled;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            typeIndex = buf.readUnsignedByte();
            enabled = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeByte(typeIndex);
            buf.writeBoolean(enabled);
        }

        public static final class Handler
                implements IMessageHandler<PetSelectionMessage, IMessage> {
            @Override
            public IMessage onMessage(final PetSelectionMessage message,
                                       final MessageContext context) {
                final EntityPlayerMP player = context.getServerHandler().playerEntity;
                IThreadListener server = (IThreadListener) player.getServerForPlayer();

                server.addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        PotassiumPetManager.apply(player,
                                PotassiumPetTypes.get(message.typeIndex),
                                message.enabled);
                    }
                });
                return null;
            }
        }
    }
}
