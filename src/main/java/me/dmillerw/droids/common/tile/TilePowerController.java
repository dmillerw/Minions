package me.dmillerw.droids.common.tile;

import me.dmillerw.droids.api.IActionProvider;
import me.dmillerw.droids.api.action.Action;
import me.dmillerw.droids.api.action.ActionSetBlock;
import me.dmillerw.droids.common.entity.EntityDroid;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

public class TilePowerController extends TileBaseComponent implements ITickable, IActionProvider {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean hasAvailableActions() {
        return true;
    }

    @Override
    public Action getNextAction(EntityDroid droid) {
        final int range = 4;
        final BlockPos start = pos.add(-range, -range, -range);
        final BlockPos end = pos.add(range, range, range);

        BlockPos target = null;
        for (BlockPos pos : BlockPos.getAllInBox(start, end)) {
            if (world.getBlockState(pos).getBlock() == Blocks.LOG) {
                target = pos;
            }
        }

        if (target != null) {
            return new ActionSetBlock(this, target);
        } else {
            return null;
        }
    }

    @Override
    public void update() {

    }
}
