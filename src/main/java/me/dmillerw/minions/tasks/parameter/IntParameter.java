package me.dmillerw.minions.tasks.parameter;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class IntParameter extends Parameter<Integer> {

    public IntParameter(String id, boolean optional) {
        super(Type.INT, 0, id, optional);
    }

    @Override
    public void writeValueToBuffer(ByteBuf buffer, Integer value) {
        buffer.writeInt(value);
    }

    @Override
    public Integer readValueFromBuffer(ByteBuf buffer) {
        return buffer.readInt();
    }

    @Override
    public void writeValueToNbt(NBTTagCompound tag, Integer value) {
        tag.setInteger(id, value);
    }

    @Override
    public Integer readValueFromNbt(NBTTagCompound tag) {
        return tag.getInteger(id);
    }
}
