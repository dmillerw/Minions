package me.dmillerw.minions.network.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.network.ServerSyncHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SRegisterStateListener implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SRegisterStateListener, IMessage> {

        @Override
        public IMessage onMessage(SRegisterStateListener message, MessageContext ctx) {
            ServerSyncHandler.INSTANCE.registerListener(ctx.getServerHandler().player.getGameProfile().getId());
            return null;
        }
    }
}
