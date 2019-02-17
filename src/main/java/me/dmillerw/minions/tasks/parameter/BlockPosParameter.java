package me.dmillerw.minions.tasks.parameter;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class BlockPosParameter extends Parameter<BlockPos> {

    public BlockPosParameter(String id, boolean optional) {
        super(Type.BLOCKPOS, BlockPos.ORIGIN, id, optional);
    }

    @Override
    public void writeValueToBuffer(ByteBuf buffer, BlockPos value) {
        buffer.writeLong(value.toLong());
    }

    @Override
    public BlockPos readValueFromBuffer(ByteBuf buffer) {
        return BlockPos.fromLong(buffer.readLong());
    }

    @Override
    public void writeValueToNbt(NBTTagCompound tag, BlockPos value) {
        tag.setLong(id, value.toLong());
    }

    @Override
    public BlockPos readValueFromNbt(NBTTagCompound tag) {
        return BlockPos.fromLong(tag.getLong(id));
    }
}
