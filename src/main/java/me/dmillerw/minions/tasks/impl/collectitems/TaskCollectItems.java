package me.dmillerw.minions.tasks.impl.collectitems;

import com.google.common.collect.Lists;
import me.dmillerw.minions.tasks.Job;
import me.dmillerw.minions.tasks.TaskDefinition;
import me.dmillerw.minions.tasks.TaskRegistry;
import me.dmillerw.minions.tasks.TaskState;
import me.dmillerw.minions.tasks.parameter.AreaParameter;
import me.dmillerw.minions.tasks.parameter.BlockPosParameter;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class TaskCollectItems extends TaskDefinition {

    public static final String HARVEST_AREA = "harvest_area";
    public static final String DROPOFF_POINT = "dropoff_point";

    public TaskCollectItems() {
        super(TaskRegistry.COLLECT_ITEMS, "Collect Items");

        addParameter(new AreaParameter(HARVEST_AREA, false));
        addParameter(new BlockPosParameter(DROPOFF_POINT, false));
    }

    @Override
    protected ItemStack getIcon() {
        return new ItemStack(Items.STICK);
    }

    @Override
    public TaskState createState(Job job) {
        return new State(job);
    }

    public static class State extends TaskState {

        private final AreaParameter harvestArea;
        private final BlockPosParameter dropoffPoint;

        private List<EntityItem> foundItems = Lists.newArrayList();
        private int itemScanDelay = 0;

        public State(Job job) {
            super(job.uuid);

            this.harvestArea = (AreaParameter) job.parameters.getParameter(HARVEST_AREA);
            this.dropoffPoint = (BlockPosParameter) job.parameters.getParameter(DROPOFF_POINT);
        }

        @Override
        public void tick(World world) {
            final Job job = job(world);

            if (itemScanDelay > 0) {
                itemScanDelay--;
            } else {
                List<EntityItem> items = job.parameters.getValue(harvestArea).getEntities(world, EntityItem.class);
                List<EntityItem> newItems = items.stream().filter((i) -> !foundItems.contains(i)).collect(Collectors.toList());

                foundItems.removeIf((i) -> !items.contains(i));
                foundItems.addAll(newItems);

                final BlockPos dropoff = job.parameters.getValue(dropoffPoint);
                newItems.forEach((i) -> job.addStep(new TaskStepCollectItem(UUID.randomUUID(), i, dropoff)));
            }
        }
    }
}
