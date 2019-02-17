package me.dmillerw.minions.entity.ai;

import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIExecuteTask extends EntityAIBase {

    private final EntityMinion minion;
    private final double speed;

    private final AIHelper aiHelper;

    public EntityAIExecuteTask(EntityMinion minion, double speed) {
        this.minion = minion;
        this.speed = speed;
        this.aiHelper = new AIHelper(minion, speed);

        this.setMutexBits(7);
    }

    @Override
    public boolean shouldExecute() {
        return minion.activeTaskStep != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

    @Override
    public void updateTask() {
        super.updateTask();

        minion.activeTaskStep.tick(minion, aiHelper);
    }
}
