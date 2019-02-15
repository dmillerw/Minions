package me.dmillerw.minions.client.data;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.entity.EntityMinion;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.UUID;

public class Minion {

    public UUID minionId;

    public String skin = "";
    public String name = "";
    public String status = "";

    public Minion() {}

    public Minion(EntityMinion minion) {
        this.minionId = minion.getUniqueID();
        this.skin = minion.getSkin();
        this.name = "Bob";
        this.status = "...";
    }

    public void writeToBuffer(ByteBuf buf) {
        buf.writeLong(minionId.getMostSignificantBits());
        buf.writeLong(minionId.getLeastSignificantBits());
        ByteBufUtils.writeUTF8String(buf, skin);
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, status);
    }

    public Minion readFromBuffer(ByteBuf buf) {
        minionId = new UUID(buf.readLong(), buf.readLong());
        skin = ByteBufUtils.readUTF8String(buf);
        name = ByteBufUtils.readUTF8String(buf);
        status = ByteBufUtils.readUTF8String(buf);
        return this;
    }
}
