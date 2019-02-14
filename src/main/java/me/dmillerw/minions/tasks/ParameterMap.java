package me.dmillerw.minions.tasks;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;

public class ParameterMap {

    public static ParameterMap fromNbt(TaskDefinition task, NBTTagCompound tagCompound) {
        ParameterMap map = new ParameterMap();
        tagCompound.getKeySet().forEach((key) -> {
            Parameter parameter = task.getParameter(key);
            map.setParameterValue(parameter, parameter.serializer.nbtToParam(tagCompound.getTag(key)));
        });

        return map;
    }

    private final Map<String, Parameter> keyToParameterMap = Maps.newHashMap();
    private final Map<String, Object> keyToValueMap = Maps.newHashMap();

    public <T> void setParameterValue(Parameter<T> parameter, T value) {
        keyToParameterMap.put(parameter.id, parameter);
        keyToValueMap.put(parameter.id, value);
    }

    public <T> T getParameter(Parameter<T> parameter) {
        return (T) keyToValueMap.get(parameter.id);
    }

    public void writeToNbt(NBTTagCompound tagCompound) {
        keyToParameterMap.keySet().forEach((key) -> {
            Parameter parameter = keyToParameterMap.get(key);
            tagCompound.setTag(key, parameter.serializer.paramToNbt(keyToValueMap.get(key)));
        });
    }
}
