package me.dmillerw.minions.tasks;

import me.dmillerw.minions.world.WorldJobData;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class TaskState {

    private final UUID job;

    public TaskState(UUID job) {
        this.job = job;
    }

    public final Job job(World world) {
        WorldJobData data = WorldJobData.getJobBoard(world);
        return data.getJob(job);
    }

    public abstract void tick(World world);
}
