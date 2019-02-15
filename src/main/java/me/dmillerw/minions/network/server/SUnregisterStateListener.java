package me.dmillerw.minions.network.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.network.ServerSyncHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SUnregisterStateListener implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SUnregisterStateListener, IMessage> {

        @Override
        public IMessage onMessage(SUnregisterStateListener message, MessageContext ctx) {
            ServerSyncHandler.INSTANCE.unregisterListener(ctx.getServerHandler().player.getGameProfile().getId());
            return null;
        }
    }
}
