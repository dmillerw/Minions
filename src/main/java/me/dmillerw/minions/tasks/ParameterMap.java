package me.dmillerw.minions.tasks;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
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
            Object value = parameter.adapter.readParameterFromBuffer(buffer);
            map.setParameterValue(parameter, value);

            System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " Reading to buffer: key => " + key + ", value => " + value);
        }

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

    public Parameter getParameterFromKey(String key) {
        return keyToParameterMap.get(key);
    }

    public <T> T getParameter(String key, Class<T> type) {
        return (T) keyToValueMap.get(key);
    }

    public <T> T getParameter(Parameter<T> parameter) {
        return (T) keyToValueMap.get(parameter.id);
    }

    public <T> void setParameterValue(Parameter<T> parameter, T value) {
        keyToParameterMap.put(parameter.id, parameter);
        keyToValueMap.put(parameter.id, value);
    }

    public void writeToBuffer(ByteBuf buffer) {
        Set<String> keys = keyToParameterMap.keySet();
        buffer.writeInt(keys.size());
        keys.forEach((key) -> {
            ByteBufUtils.writeUTF8String(buffer, key);
            Parameter parameter = keyToParameterMap.get(key);
            parameter.adapter.writeParameterToBuffer(buffer, keyToValueMap.get(key));

            System.out.println(FMLCommonHandler.instance().getEffectiveSide() + " Writing to buffer: key => " + key + ", value => " + keyToValueMap.get(key));
        });
    }

    public void writeToNbt(NBTTagCompound tagCompound) {
        keyToParameterMap.keySet().forEach((key) -> {
            Parameter parameter = keyToParameterMap.get(key);
            tagCompound.setTag(key, parameter.adapter.writeParameterToNbtTag(keyToValueMap.get(key)));
        });
    }
}
