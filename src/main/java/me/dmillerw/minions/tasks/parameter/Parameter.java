package me.dmillerw.minions.tasks.parameter;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Objects;

public abstract class Parameter<V> {

    public static enum Type {
        
        INT, AREA, BLOCKPOS
    }
    
    public final Type type;
    public final V defaultValue;
    public final String id;
    public final boolean optional;

    public Parameter(Type type, V defaultValue, String id, boolean optional) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.id = id;
        this.optional = optional;
    }

    public abstract void writeValueToBuffer(ByteBuf buffer, V value);
    public abstract V readValueFromBuffer(ByteBuf buffer);
    public abstract void writeValueToNbt(NBTTagCompound tag, V value);
    public abstract V readValueFromNbt(NBTTagCompound tag);

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
