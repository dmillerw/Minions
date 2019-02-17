package me.dmillerw.minions.tasks.impl.collectitems;

import com.google.common.collect.Maps;
import me.dmillerw.minions.tasks.Job;
import me.dmillerw.minions.tasks.TaskDefinition;
import me.dmillerw.minions.tasks.TaskState;
import me.dmillerw.minions.tasks.parameter.AreaParameter;
import me.dmillerw.minions.util.Area;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

import java.util.Map;
import java.util.UUID;

public class TaskHarvestFlowers extends TaskDefinition {

    public static final String HARVEST_AREA = "harvest_area";

    public TaskHarvestFlowers() {
        super("harvest_flowers", "Harvest Flowers");

        addParameter(new AreaParameter(HARVEST_AREA, false));
    }

    @Override
    protected ItemStack getIcon() {
        return new ItemStack(Blocks.RED_FLOWER);
    }

    @Override
    public TaskState createState(Job job) {
        return new State(job);
    }

    public static class State extends TaskState {

        private final AreaParameter harvestArea;

        private Map<BlockPos, Block> scannedBlocks = Maps.newHashMap();
        private int scanDelay = 0;

        public State(Job job) {
            super(job.uuid);

            this.harvestArea = (AreaParameter) job.parameters.getParameter(HARVEST_AREA);
        }

        @Override
        public void tick(World world) {
            final Job job = job(world);

            if (scanDelay > 0) {
                scanDelay--;
            } else {
                Area area = job.parameters.getValue(harvestArea);
                area.getBlocksInside().forEach((position) -> {
                    boolean harvest = false;

                    if (!scannedBlocks.containsKey(position)) {
                        harvest = true;
                    } else {
                        Block scanned = scannedBlocks.get(position);
                        IBlockState state = world.getBlockState(position);
                        if (state.getBlock() != scanned) {
                            harvest = true;
                        }
                    }

                    if (harvest) {
                        IBlockState state = world.getBlockState(position);
                        if (!world.isAirBlock(position)) {
                            if (state.getBlock() instanceof IPlantable) {
                                job.addStep(new TaskStepBreakBlock(UUID.randomUUID(), position));
                            }
                        }
                    }
                });
            }
        }
    }
}
