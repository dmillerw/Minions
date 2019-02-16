package me.dmillerw.minions.network.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.tasks.Job;
import me.dmillerw.minions.world.WorldJobData;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SUpdateJob implements IMessage {

    public Job job;

    @Override
    public void fromBytes(ByteBuf buf) {
        job = Job.fromBuffer(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        job.writeToBuffer(buf);
    }

    public static class Handler implements IMessageHandler<SUpdateJob, IMessage> {

        @Override
        public IMessage onMessage(SUpdateJob message, MessageContext ctx) {
            PacketHandler.addScheduledTask(ctx.netHandler, () -> {
                WorldJobData data = WorldJobData.getJobBoard(ctx.getServerHandler().player.world);
                data.addJob(message.job);
            });
            return null;
        }
    }
}
