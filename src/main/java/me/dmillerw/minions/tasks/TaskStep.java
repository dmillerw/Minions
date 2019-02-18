package me.dmillerw.minions.tasks;

import com.google.common.collect.Sets;
import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A task step defines one singular action that a task requires
 * A step may either complete the requirements of a job by itself, or break up into multiple steps
 */
public abstract class TaskStep {

    public final UUID uuid;
    public boolean claimed = false;

    private Set<UUID> minionBlacklist = Sets.newHashSet();

    public TaskStep(UUID uuid) {
        this.uuid = uuid;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public void blacklist(EntityMinion minion) {
        minionBlacklist.add(minion.getUniqueID());
    }

    public abstract boolean canPerform(EntityMinion minion);

    public abstract boolean shouldDie(World world);
    public abstract void tick(EntityMinion entity);

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
