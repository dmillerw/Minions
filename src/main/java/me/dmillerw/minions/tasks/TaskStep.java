package me.dmillerw.minions.tasks;

import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.entity.ai.AIHelper;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.UUID;

/**
 * A task step defines one singular action that a task requires
 * A step may either complete the requirements of a job by itself, or break up into multiple steps
 */
public abstract class TaskStep {

    public final UUID uuid;
    public boolean claimed = false;

    public TaskStep(UUID uuid) {
        this.uuid = uuid;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public abstract boolean shouldDie(World world);
    public abstract void tick(EntityMinion entity, AIHelper helper);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskStep taskStep = (TaskStep) o;
        return Objects.equals(uuid, taskStep.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
