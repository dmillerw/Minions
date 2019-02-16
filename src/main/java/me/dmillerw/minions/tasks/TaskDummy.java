package me.dmillerw.minions.tasks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class TaskDummy extends TaskDefinition {

    public static final Parameter<Integer> DUMMY = Parameter.newParameter("dummy", Parameter.INT, 0);

    public TaskDummy(int i) {
        super("dummy_" + i, "Dummy #" + i);

        addParameter(DUMMY);
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
