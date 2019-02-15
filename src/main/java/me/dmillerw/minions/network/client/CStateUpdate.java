package me.dmillerw.minions.network.client;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.client.data.ClientSyncHandler;
import me.dmillerw.minions.client.data.Minion;
import me.dmillerw.minions.tasks.Job;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CStateUpdate {

    public static enum Type {

        ADD, REMOVE, MODIFY;

        public static Type fromInt(int i) {
            switch (i) {
                case 2: return MODIFY;
                case 1: return REMOVE;
                case 0:
                default: return ADD;
            }
        }
    }

    public static class JobUpdate implements IMessage {

        public Type type;
        public Job job;

        @Override
        public void fromBytes(ByteBuf buf) {
            type = Type.fromInt(buf.readInt());
            job = Job.fromBuffer(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(type.ordinal());
            job.writeToBuffer(buf);
        }

        public static class Handler implements IMessageHandler<JobUpdate, IMessage> {

            @Override
            public IMessage onMessage(JobUpdate message, MessageContext ctx) {
                if (message.type == Type.REMOVE) {
                    ClientSyncHandler.INSTANCE.removeJob(message.job);
                } else {
                    ClientSyncHandler.INSTANCE.updateJob(message.job);
                }

                return null;
            }
        }
    }

    public static class MinionUpdate implements IMessage {

        public Type type;
        public Minion minion;

        @Override
        public void fromBytes(ByteBuf buf) {
            type = Type.fromInt(buf.readInt());
            minion = new Minion().readFromBuffer(buf);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(type.ordinal());
            minion.writeToBuffer(buf);
        }

        public static class Handler implements IMessageHandler<MinionUpdate, IMessage> {

            @Override
            public IMessage onMessage(MinionUpdate message, MessageContext ctx) {
                if (message.type == Type.REMOVE) {
                    ClientSyncHandler.INSTANCE.removeMinion(message.minion);
                } else {
                    ClientSyncHandler.INSTANCE.updateMinion(message.minion);
                }

                return null;
            }
        }
    }
}
