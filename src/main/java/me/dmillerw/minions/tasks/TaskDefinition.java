package me.dmillerw.minions.tasks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class TaskDefinition {

    public final String id;
    public final String name;

    private Map<String, Parameter> keyToParameterMap = Maps.newHashMap();
    private Set<Parameter> parameters = Sets.newHashSet();

    private ItemStack icon;

    public TaskDefinition(String id, String name) {
        this.id = id;
        this.name = name;
        this.icon = getIcon();
    }

    protected void addParameter(Parameter parameter) {
        this.keyToParameterMap.put(parameter.id, parameter);
        this.parameters.add(parameter);
    }

    public Parameter getParameter(String key) {
        return keyToParameterMap.get(key);
    }

    public ImmutableSet<Parameter> getParameters() {
        return ImmutableSet.copyOf(parameters);
    }

    @SideOnly(Side.CLIENT)
    protected abstract ItemStack getIcon();

    @SideOnly(Side.CLIENT)
    public final ItemStack getRenderIcon() {
        return icon;
    }

    public abstract TaskInstance createInstance(UUID jobUuid, ParameterMap parameters);
}
