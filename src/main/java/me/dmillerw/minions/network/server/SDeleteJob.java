package me.dmillerw.minions.network.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.world.WorldJobData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class SDeleteJob implements IMessage {

    private UUID targetUuid;

    public SDeleteJob() {}

    public SDeleteJob(UUID targetUuid) {
        this.targetUuid = targetUuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        targetUuid = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(targetUuid.getMostSignificantBits());
        buf.writeLong(targetUuid.getLeastSignificantBits());
    }

    public static class Handler implements IMessageHandler<SDeleteJob, IMessage> {

        @Override
        public IMessage onMessage(SDeleteJob message, MessageContext ctx) {
            PacketHandler.addScheduledTask(ctx.netHandler, () -> {
                PacketHandler.addScheduledTask(ctx.netHandler, () -> {
                    WorldJobData data = WorldJobData.getJobBoard(ctx.getServerHandler().player.world);
                    data.deleteJob(message.targetUuid);
                });
            });
            return null;
        }
    }
}
