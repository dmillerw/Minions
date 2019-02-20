package me.dmillerw.droids.common.entity.ai;

import me.dmillerw.droids.api.action.Action;
import me.dmillerw.droids.common.entity.EntityDroid;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIExecuteTask extends EntityAIBase {

    private final EntityDroid droid;
    public final double speed;

    public EntityAIExecuteTask(EntityDroid droid, double speed) {
        this.droid = droid;
        this.speed = speed;

        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        return droid.getAction() != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

    @Override
    public void updateTask() {
        if (droid.world.isRemote)
            return;

        Action action = droid.getAction();
        if (action != null) {
            action.tick(droid);
        }
    }
}
