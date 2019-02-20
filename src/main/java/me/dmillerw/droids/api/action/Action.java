package me.dmillerw.droids.api.action;

import me.dmillerw.droids.api.IActionProvider;
import me.dmillerw.droids.common.entity.EntityDroid;

public abstract class Action {

    protected IActionProvider actionProvider;

    private String key;
    private EntityDroid droid;
    private ActionState.Progress progress = ActionState.Progress.INIITIAL;

    public Action(IActionProvider actionProvider) {
        this.actionProvider = actionProvider;
    }

    public IActionProvider getActionProvider() {
        return actionProvider;
    }

    public String getKey() {
        if (key == null) {
            key = getUniqueKey();
        }
        return key;
    }

    public EntityDroid getDroid() {
        return droid;
    }

    public void setDroid(EntityDroid droid) {
        this.droid = droid;
    }

    public ActionState.Progress getProgress() {
        return progress;
    }

    public void setProgress(ActionState.Progress progress) {
        this.progress = progress;
        this.actionProvider.getNetwork().updateAction(this);
    }

    /**
     * An arbitrary key based on whatever the 'contents' of the action is (the position to move to, the block to break, etc)
     * It can be whatever you like, but it be unique to each actual interaction you intend to cause in the world
     * Failure to do so can result in your action being run multiple times for the same thing
     *
     * Once the method is run, the key cannot be changed. Doing so will break things
     */
    protected abstract String getUniqueKey();

    public abstract void tick(EntityDroid droid);
}
