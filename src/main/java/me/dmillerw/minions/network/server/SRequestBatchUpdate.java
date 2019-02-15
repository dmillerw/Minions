package me.dmillerw.minions.network.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.client.data.Minion;
import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.network.client.CBatchUpdate;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SRequestBatchUpdate implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<SRequestBatchUpdate, IMessage> {

        @Override
        public IMessage onMessage(SRequestBatchUpdate message, MessageContext ctx) {
            PacketHandler.addScheduledTask(ctx.netHandler, () -> {
                CBatchUpdate packet = new CBatchUpdate();
                packet.minions = ctx.getServerHandler().player.world.getLoadedEntityList().stream()
                        .filter((entity -> entity instanceof EntityMinion))
                        .map((entity -> {
                            Minion minion = new Minion();
                            minion.minionId = entity.getUniqueID();
                            minion.skin = ((EntityMinion) entity).getSkin();
                            minion.name = "Minion";
                            minion.status = "...";

                            return minion;
                        })).toArray(Minion[]::new);

//                WorldJobData data = WorldJobData.getJobBoard(ctx.getServerHandler().player.world);
//                packet.jobs = data.getJobs().stream().map((posting) -> {
//                    Job job = new Job();
//
//                    job.jobId = posting.uuid;
//                    job.taskId = posting.task.id;
//                    job.title = "";
//                    job.status = "";
//
//                    return job;
//                }).toArray(Job[]::new);
//
                PacketHandler.INSTANCE.sendTo(packet, ctx.getServerHandler().player);
            });
            return null;
        }
    }
}
