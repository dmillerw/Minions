package me.dmillerw.minions.tasks;

import me.dmillerw.minions.util.Area;
import me.dmillerw.minions.util.NBTUtils;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class Parameter<T> {

    public static <T, N extends NBTBase> Parameter<T> newParameter(String id, ParameterSerializer<T, N> serializer) {
        return new Parameter<>(id, serializer);
    }

    public static <T, N extends NBTBase> Parameter<T> newOptionalParameter(String id, ParameterSerializer<T, N> serializer) {
        return new Parameter<>(id, serializer, true);
    }

    public static abstract class ParameterSerializer<T, N extends NBTBase> {

        public abstract N paramToNbt(T param);

        public abstract T nbtToParam(N tag);
    }

    public static final ParameterSerializer<Integer, NBTTagInt> INT = new ParameterSerializer<Integer, NBTTagInt>() {
        @Override
        public NBTTagInt paramToNbt(Integer param) {
            return new NBTTagInt(param);
        }

        @Override
        public Integer nbtToParam(NBTTagInt tag) {
            return tag.getInt();
        }
    };

    public static final ParameterSerializer<Area, NBTTagCompound> AREA = new ParameterSerializer<Area, NBTTagCompound>() {
        @Override
        public NBTTagCompound paramToNbt(Area param) {
            return param.toNbt();
        }

        @Override
        public Area nbtToParam(NBTTagCompound tag) {
            return Area.fromNbt(tag);
        }
    };

    public static final ParameterSerializer<BlockPos, NBTTagLong> BLOCKPOS = new ParameterSerializer<BlockPos, NBTTagLong>() {
        @Override
        public NBTTagLong paramToNbt(BlockPos param) {
            return new NBTTagLong(param.toLong());
        }

        @Override
        public BlockPos nbtToParam(NBTTagLong tag) {
            return BlockPos.fromLong(tag.getLong());
        }
    };

    public final String id;
    public final ParameterSerializer<T, ?> serializer;
    public final boolean optional;

    private Parameter(String id, ParameterSerializer<T, ?> serializer) {
        this(id, serializer, false);
    }

    private Parameter(String id, ParameterSerializer<T, ?> serializer, boolean optional) {
        this.id = id;
        this.serializer = serializer;
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
