package me.dmillerw.minions.network.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.util.MinionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class SUpdateMinion implements IMessage {

    public UUID targetUuid;
    public String name;
    public MinionType minionType;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(targetUuid.getMostSignificantBits());
        buf.writeLong(targetUuid.getLeastSignificantBits());
        if (name == null || name.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            ByteBufUtils.writeUTF8String(buf, name);
        }
        if (minionType == null) {
            ByteBufUtils.writeUTF8String(buf, MinionType.ANYONE.getString());
        } else {
            ByteBufUtils.writeUTF8String(buf, minionType.getString());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        targetUuid = new UUID(buf.readLong(), buf.readLong());
        if (buf.readBoolean()) {
            name = ByteBufUtils.readUTF8String(buf);
        } else {
            name = "";
        }
        minionType = MinionType.fromString(ByteBufUtils.readUTF8String(buf));
    }

    public static class Handler implements IMessageHandler<SUpdateMinion, IMessage> {

        @Override
        public IMessage onMessage(SUpdateMinion message, MessageContext ctx) {
            PacketHandler.addScheduledTask(ctx.netHandler, () -> {
                World world = ctx.getServerHandler().player.world;
                world.getLoadedEntityList()
                        .stream()
                        .filter((entity) -> entity.getUniqueID().equals(message.targetUuid))
                        .findFirst()
                        .ifPresent((entity) -> {
                            ((EntityMinion) entity).setSkin(message.name);
                            ((EntityMinion) entity).setMinionType(message.minionType);
                        });
            });

            return null;
        }
    }
}
