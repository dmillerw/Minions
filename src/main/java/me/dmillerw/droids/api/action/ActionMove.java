package me.dmillerw.droids.api.action;

import me.dmillerw.droids.api.IActionProvider;
import me.dmillerw.droids.common.entity.EntityDroid;
import net.minecraft.util.math.BlockPos;

public class ActionMove extends Action {

    public static final String KEY = "move";

    private BlockPos targetPoint;

    public ActionMove(IActionProvider parent) {
        super(parent);
    }

    public ActionMove(IActionProvider parent, BlockPos targetPoint) {
        super(parent);

        this.targetPoint = targetPoint;
    }

    public BlockPos getTargetPoint() {
        return targetPoint;
    }

    public void setTargetPoint(BlockPos targetPoint) {
        this.targetPoint = targetPoint;
    }

    @Override
    public String getUniqueKey() {
        return KEY + ":" + targetPoint.toLong();
    }

    @Override
    public void tick(EntityDroid droid) {
        if (!droid.aiHelper.moveToPosition(targetPoint)) {
            setProgress(ActionState.Progress.CANCELLED);
        } else {
            if (droid.getDistanceSq(targetPoint) < 1)
                setProgress(ActionState.Progress.COMPLETE);
        }
    }
}
