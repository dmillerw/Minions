package me.dmillerw.minions.tasks;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ParameterMap {

    public static ParameterMap fromBuffer(TaskDefinition task, ByteBuf buffer) {
        String[] keys = ByteBufUtils.readUTF8String(buffer).split(";");
        ParameterMap map = new ParameterMap(task);
        Arrays.stream(keys).forEach((key) -> {
            Parameter parameter = task.getParameter(key);
            map.setParameterValue(parameter, parameter.adapter.readParameterFromBuffer(buffer));
        });

        return map;
    }

    public static ParameterMap fromNbt(TaskDefinition task, NBTTagCompound tagCompound) {
        ParameterMap map = new ParameterMap(task);
        tagCompound.getKeySet().forEach((key) -> {
            Parameter parameter = task.getParameter(key);
            map.setParameterValue(parameter, parameter.adapter.readParameterFromNbtTag(tagCompound.getTag(key)));
        });

        return map;
    }

    private final Map<String, Parameter> keyToParameterMap = Maps.newHashMap();
    private final Map<String, Object> keyToValueMap = Maps.newHashMap();

    public ParameterMap(TaskDefinition task) {
        loadFromTask(task);
    }

    private void loadFromTask(TaskDefinition task) {
        task.getParameters().forEach((parameter -> setParameterValue(parameter, parameter.defaultValue)));
    }

    public Set<String> getKeys() {
        return keyToParameterMap.keySet();
    }

    public <T> T getParameter(Parameter<T> parameter) {
        return (T) keyToValueMap.get(parameter.id);
    }

    public <T> void setParameterValue(Parameter<T> parameter, T value) {
        keyToParameterMap.put(parameter.id, parameter);
        keyToValueMap.put(parameter.id, value);
    }

    public void writeToBuffer(ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, Joiner.on(";").join(keyToParameterMap.keySet()));
        keyToParameterMap.keySet().forEach((key) -> {
            Parameter parameter = keyToParameterMap.get(key);
            parameter.adapter.writeParameterToBuffer(buffer, keyToValueMap.get(key));
        });
    }

    public void writeToNbt(NBTTagCompound tagCompound) {
        keyToParameterMap.keySet().forEach((key) -> {
            Parameter parameter = keyToParameterMap.get(key);
            tagCompound.setTag(key, parameter.adapter.writeParameterToNbtTag(keyToValueMap.get(key)));
        });
    }
}
