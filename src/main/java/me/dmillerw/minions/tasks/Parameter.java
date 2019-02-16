package me.dmillerw.minions.tasks;

import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.util.Area;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class Parameter<T> {

    public static final String TYPE_INT = "int";
    public static final String TYPE_AREA = "area";
    public static final String TYPE_BLOCKPOS = "blockpos";

    public static <T> Parameter<T> newParameter(String id, ParameterAdapter<T> serializer, T defaultValue) {
        return new Parameter<T>(id, serializer, defaultValue, false);
    }

    public static abstract class ParameterAdapter<T> {

        public abstract Class getParameterType();

        public abstract T readParameterFromBuffer(ByteBuf buffer);

        public abstract void writeParameterToBuffer(ByteBuf buffer, T param);

        public abstract NBTBase writeParameterToNbtTag(T param);

        public abstract T readParameterFromNbtTag(NBTBase tag);
    }

    public static final ParameterAdapter<Integer> INT = new ParameterAdapter<Integer>() {
        @Override
        public Class getParameterType() {
            return int.class;
        }

        @Override
        public Integer readParameterFromBuffer(ByteBuf buffer) {
            return buffer.readInt();
        }

        @Override
        public void writeParameterToBuffer(ByteBuf buffer, Integer param) {
            buffer.writeInt(param);
        }

        @Override
        public NBTBase writeParameterToNbtTag(Integer param) {
            return new NBTTagInt(param);
        }

        @Override
        public Integer readParameterFromNbtTag(NBTBase tag) {
            return ((NBTTagInt) tag).getInt();
        }
    };

    public static final ParameterAdapter<Area> AREA = new ParameterAdapter<Area>() {
        @Override
        public Class getParameterType() {
            return Area.class;
        }

        @Override
        public Area readParameterFromBuffer(ByteBuf buffer) {
            return Area.fromBuffer(buffer);
        }

        @Override
        public void writeParameterToBuffer(ByteBuf buffer, Area param) {
            param.writeToBuffer(buffer);
        }

        @Override
        public NBTBase writeParameterToNbtTag(Area param) {
            return param.toNbt();
        }

        @Override
        public Area readParameterFromNbtTag(NBTBase tag) {
            return Area.fromNbt((NBTTagCompound) tag);
        }
    };

    public static final ParameterAdapter<BlockPos> BLOCKPOS = new ParameterAdapter<BlockPos>() {
        @Override
        public Class getParameterType() {
            return BlockPos.class;
        }

        @Override
        public BlockPos readParameterFromBuffer(ByteBuf buffer) {
            return BlockPos.fromLong(buffer.readLong());
        }

        @Override
        public void writeParameterToBuffer(ByteBuf buffer, BlockPos param) {
            buffer.writeLong(param.toLong());
        }

        @Override
        public NBTBase writeParameterToNbtTag(BlockPos param) {
            return new NBTTagLong(param.toLong());
        }

        @Override
        public BlockPos readParameterFromNbtTag(NBTBase tag) {
            return BlockPos.fromLong(((NBTTagLong) tag).getLong());
        }
    };

    public final String id;
    public final ParameterAdapter<T> adapter;
    public final T defaultValue;
    public final boolean optional;

    private Parameter(String id, ParameterAdapter<T> adapter, T defaultValue, boolean optional) {
        this.id = id;
        this.adapter = adapter;
        this.defaultValue = defaultValue;
        this.optional = optional;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter<?> parameter = (Parameter<?>) o;
        return Objects.equals(id, parameter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
