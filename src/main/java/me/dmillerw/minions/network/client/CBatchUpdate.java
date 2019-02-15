package me.dmillerw.minions.network.client;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.client.data.ClientSyncHandler;
import me.dmillerw.minions.client.data.Minion;
import me.dmillerw.minions.tasks.Job;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CBatchUpdate implements IMessage {

    public Minion[] minions = new Minion[0];
    public Job[] jobs = new Job[0];

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(minions.length);
        for (Minion minion : minions)
            minion.writeToBuffer(buf);
        buf.writeInt(jobs.length);
        for (Job job : jobs)
            job.writeToBuffer(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        minions = new Minion[size];
        for (int i=0; i<size; i++) {
            minions[i] = new Minion().readFromBuffer(buf);
        }
        size = buf.readInt();
        jobs = new Job[size];
        for (int i=0; i<size; i++) {
            jobs[i] = Job.fromBuffer(buf);
        }
    }

    public static class Handler implements IMessageHandler<CBatchUpdate, IMessage> {

        @Override
        public IMessage onMessage(CBatchUpdate message, MessageContext ctx) {
            ClientSyncHandler.INSTANCE.handleBatchUpdate(message.minions, message.jobs);
            return null;
        }
    }
}
