package me.dmillerw.minions.entity.ai;

import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.util.math.BlockPos;

public class AIHelper {

    private final EntityMinion minion;
    private final double speed;

    public AIHelper(EntityMinion minion, double speed) {
        this.minion = minion;
        this.speed = speed;
    }

    public void moveToPosition(BlockPos pos) {

    }
}
