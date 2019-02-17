package me.dmillerw.minions.tasks.parameter;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.tasks.TaskDefinition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Map;
import java.util.Set;

public class ParameterMap {

    public static ParameterMap fromBuffer(TaskDefinition task, ByteBuf buffer) {
        ParameterMap map = new ParameterMap(task);

        int size = buffer.readInt();
        for (int i=0; i<size; i++) {
            String key = ByteBufUtils.readUTF8String(buffer);
            Parameter parameter = task.getParameter(key);
            Object value = parameter.readValueFromBuffer(buffer);
            map.setValue(parameter, value);
        }

        return map;
    }

    public static ParameterMap fromNbt(TaskDefinition task, NBTTagCompound tagCompound) {
        ParameterMap map = new ParameterMap(task);
        tagCompound.getKeySet().forEach((key) -> {
            Parameter parameter = task.getParameter(key);
            map.setValue(parameter, parameter.readValueFromNbt(tagCompound));
        });

        return map;
    }

    private final Map<String, Parameter> keyToParameterMap = Maps.newHashMap();
    private final Map<String, Object> keyToValueMap = Maps.newHashMap();

    public ParameterMap(TaskDefinition task) {
        loadFromTask(task);
    }

    private void loadFromTask(TaskDefinition task) {
        task.getParameters().forEach((parameter -> setValue(parameter, parameter.defaultValue)));
    }

    public Set<String> getKeys() {
        return keyToParameterMap.keySet();
    }

    public Parameter getParameter(String key) {
        return keyToParameterMap.get(key);
    }

    public <V> V getValue(Parameter<V> key) {
        return (V) keyToValueMap.get(key.id);
    }

    public <V> void setValue(Parameter<V> parameter, V value) {
        this.keyToParameterMap.put(parameter.id, parameter);
        this.keyToValueMap.put(parameter.id, value);
    }

    public void writeToBuffer(ByteBuf buffer) {
        Set<String> keys = keyToParameterMap.keySet();
        buffer.writeInt(keys.size());
        keys.forEach((key) -> {
            ByteBufUtils.writeUTF8String(buffer, key);
            Parameter parameter = keyToParameterMap.get(key);
            parameter.writeValueToBuffer(buffer, keyToValueMap.get(key));
        });
    }

    public void writeToNbt(NBTTagCompound tagCompound) {
        keyToParameterMap.keySet().forEach((key) -> {
            Parameter parameter = keyToParameterMap.get(key);
            parameter.writeValueToNbt(tagCompound, keyToValueMap.get(key));
        });
    }
}
