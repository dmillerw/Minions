package me.dmillerw.droids.common.tile;

import me.dmillerw.droids.api.ClaimedObjects;
import me.dmillerw.droids.api.IActionProvider;
import me.dmillerw.droids.api.action.Action;
import me.dmillerw.droids.common.entity.EntityDroid;

public class TileFluidController extends TileBaseComponent implements IActionProvider {

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean hasAvailableActions() {
        return false;
    }

    @Override
    public Action getNextAction(ClaimedObjects objects, EntityDroid droid) {
        return null;
    }

    @Override
    public void onActionUpdate(ClaimedObjects objects, Action action) {

    }


}
