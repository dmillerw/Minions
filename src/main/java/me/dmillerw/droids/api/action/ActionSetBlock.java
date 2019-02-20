package me.dmillerw.droids.api.action;

import me.dmillerw.droids.api.IActionProvider;
import me.dmillerw.droids.common.entity.EntityDroid;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class ActionSetBlock extends Action {

    public static final String KEY = "setblock";

    private BlockPos targetPos;

    public ActionSetBlock(IActionProvider actionProvider) {
        super(actionProvider);
    }

    public ActionSetBlock(IActionProvider actionProvider, BlockPos targetPos) {
        super(actionProvider);

        this.targetPos = targetPos;
    }

    @Override
    protected String getUniqueKey() {
        return KEY + ":" + targetPos.toLong();
    }

    @Override
    public void tick(EntityDroid droid) {
        if (!droid.aiHelper.moveToPosition(targetPos)) {
            setProgress(ActionState.Progress.CANCELLED);
        }

        if (droid.getDistanceSq(targetPos) < 2) {
            droid.world.setBlockState(targetPos, Blocks.DIAMOND_BLOCK.getDefaultState());
            setProgress(ActionState.Progress.COMPLETE);
        }
    }
}
