package me.dmillerw.minions.tasks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class TaskDummy extends TaskDefinition {

    public TaskDummy(int i) {
        super("dummy_" + i, "Dummy #" + i);
    }

    @Override
    protected ItemStack getIcon() {
        return new ItemStack(Items.DIAMOND);
    }

    @Override
    public TaskInstance createInstance(UUID jobUuid, ParameterMap parameters) {
        return null;
    }
}
