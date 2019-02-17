package me.dmillerw.minions.entity.ai;

import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class AIHelper {

    private final EntityMinion minion;
    private final double speed;

    public AIHelper(EntityMinion minion, double speed) {
        this.minion = minion;
        this.speed = speed;
    }

    public void moveToPosition(BlockPos pos) {
        boolean moving = minion.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), .45);
    }

    public void moveToEntity(Entity entity) {
        boolean moving = minion.getNavigator().tryMoveToEntityLiving(entity, .45);
    }

    public void finishStep() {

    }
}
