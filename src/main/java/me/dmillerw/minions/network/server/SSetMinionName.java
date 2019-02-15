package me.dmillerw.minions.network.server;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.network.PacketHandler;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class SSetMinionName implements IMessage {

    public UUID targetUuid;
    public String name;

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
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        targetUuid = new UUID(buf.readLong(), buf.readLong());
        if (buf.readBoolean()) {
            name = ByteBufUtils.readUTF8String(buf);
        } else {
            name = "";
        }
    }

    public static class Handler implements IMessageHandler<SSetMinionName, IMessage> {

        @Override
        public IMessage onMessage(SSetMinionName message, MessageContext ctx) {
            PacketHandler.addScheduledTask(ctx.netHandler, () -> {
                World world = ctx.getServerHandler().player.world;
                world.getLoadedEntityList()
                        .stream()
                        .filter((entity) -> entity.getUniqueID().equals(message.targetUuid))
                        .findFirst()
                        .ifPresent((entity) -> ((EntityMinion) entity).setSkin(message.name));
            });

            return null;
        }
    }
}
