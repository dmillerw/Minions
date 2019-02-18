package me.dmillerw.minions.entity.ai;

import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIExecuteTask extends EntityAIBase {

    private final EntityMinion minion;
    public final double speed;

    public EntityAIExecuteTask(EntityMinion minion, double speed) {
        this.minion = minion;
        this.speed = speed;

        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

    @Override
    public void updateTask() {
        super.updateTask();

        if (minion.activeTaskStep != null)
            minion.activeTaskStep.tick(minion);
    }
}
