package me.dmillerw.minions.tasks.parameter;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.util.Area;
import net.minecraft.nbt.NBTTagCompound;

public class AreaParameter extends Parameter<Area> {

    public AreaParameter(String id, boolean optional) {
        super(Type.AREA, Area.ORIGIN, id, optional);
    }

    @Override
    public void writeValueToBuffer(ByteBuf buffer, Area value) {
        value.writeToBuffer(buffer);
    }

    @Override
    public Area readValueFromBuffer(ByteBuf buffer) {
        return Area.fromBuffer(buffer);
    }

    @Override
    public void writeValueToNbt(NBTTagCompound tag, Area value) {
        tag.setTag(id, value.toNbt());
    }

    @Override
    public Area readValueFromNbt(NBTTagCompound tag) {
        return Area.fromNbt(tag.getCompoundTag(id));
    }
}
