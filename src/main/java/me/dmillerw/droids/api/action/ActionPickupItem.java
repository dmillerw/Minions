package me.dmillerw.droids.api.action;

import me.dmillerw.droids.api.IActionProvider;
import me.dmillerw.droids.common.entity.EntityDroid;
import net.minecraft.entity.item.EntityItem;

public class ActionPickupItem extends Action {

    public static final String KEY = "pickup_item";

    private EntityItem targetItem;

    public ActionPickupItem(IActionProvider parent) {
        super(parent);
    }

    public ActionPickupItem(IActionProvider parent, EntityItem targetItem) {
        super(parent);

        this.targetItem = targetItem;
    }

    public EntityItem getTargetItem() {
        return targetItem;
    }

    public void setTargetItem(EntityItem targetItem) {
        this.targetItem = targetItem;
    }

    @Override
    public String getUniqueKey() {
        return KEY + ":" + targetItem.getUniqueID().toString();
    }

    @Override
    public void tick(EntityDroid droid) {
        if (targetItem.isDead)
            setProgress(ActionState.Progress.CANCELLED);

        if (droid.aiHelper.pickupItem(targetItem))
            setProgress(ActionState.Progress.COMPLETE);
    }
}
