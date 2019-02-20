package me.dmillerw.droids.api;

import me.dmillerw.droids.api.action.Action;
import me.dmillerw.droids.common.entity.EntityDroid;

public interface IActionProvider extends INetworkComponent {

    public int getPriority();
    public boolean hasAvailableActions();
    public Action getNextAction(ClaimedObjects objects, EntityDroid droid);
    public void onActionUpdate(ClaimedObjects objects, Action action);
}
