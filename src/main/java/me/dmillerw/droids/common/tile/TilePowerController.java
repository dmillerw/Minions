package me.dmillerw.droids.common.tile;

import me.dmillerw.droids.api.ClaimedObjects;
import me.dmillerw.droids.api.IActionProvider;
import me.dmillerw.droids.api.action.Action;
import me.dmillerw.droids.api.action.ActionPickupItem;
import me.dmillerw.droids.api.action.ActionState;
import me.dmillerw.droids.common.entity.EntityDroid;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

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
    public Action getNextAction(ClaimedObjects claimedObjects, EntityDroid droid) {
        final int range = 4;
        final BlockPos start = pos.add(-range, -range, -range);
        final BlockPos end = pos.add(range, range, range);

//        for (BlockPos pos : BlockPos.getAllInBox(start, end)) {
//            if (world.getBlockState(pos).getBlock() == Blocks.LOG) {
//                if (claimedObjects.claimBlock(pos)) {
//                    return new ActionSetBlock(this, pos);
//                }
//            }
//        }

        List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(start, end));
        if (items == null || items.isEmpty())
            return null;

        for (EntityItem item : items) {
            if (claimedObjects.claimEntity(item)) {
                return new ActionPickupItem(this, item);
            }
        }

        return null;
    }

    @Override
    public void onActionUpdate(ClaimedObjects claimedObjects, Action action) {
        if (action.getProgress() == ActionState.Progress.CANCELLED || action.getProgress() == ActionState.Progress.COMPLETE)
            claimedObjects.releaseEntity(((ActionPickupItem)action).getTargetItem());
    }

    @Override
    public void update() {

    }
}
