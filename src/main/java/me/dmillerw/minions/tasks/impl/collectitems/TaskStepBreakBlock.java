package me.dmillerw.minions.tasks.impl.collectitems;

import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.entity.ai.AIHelper;
import me.dmillerw.minions.tasks.TaskStep;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class TaskStepBreakBlock extends TaskStep {

    private final BlockPos target;

    public TaskStepBreakBlock(UUID uuid, BlockPos target) {
        super(uuid);
        this.target = target;
    }

    @Override
    public boolean canPerform(EntityMinion minion) {
        return minion.aiHelper.canPathTo(target);
    }

    @Override
    public boolean shouldDie(World world) {
        return world.isAirBlock(target);
    }

    @Override
    public void tick(EntityMinion minion) {
        AIHelper helper = minion.aiHelper;
        if (minion.getPosition().equals(target)) {
            NonNullList<ItemStack> drops = NonNullList.create();
            IBlockState state = minion.world.getBlockState(target);
            Block block = state.getBlock();

            block.getDrops(drops, minion.world, target, state, 0);
            drops.forEach((stack) -> {
                EntityItem item = new EntityItem(minion.world, minion.posX, minion.posY, minion.posZ, stack.copy());
                item.setPosition(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
                item.setVelocity(0, 0, 0);
                minion.world.spawnEntity(item);
            });

            minion.world.setBlockToAir(target);
            minion.activeTaskStep = null;
        } else {
            helper.moveToPosition(target);
        }
    }
}
