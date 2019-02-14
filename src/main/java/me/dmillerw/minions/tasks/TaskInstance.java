package me.dmillerw.minions.tasks;

import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.entity.ai.AIHelper;

import java.util.UUID;

public abstract class TaskInstance {

    // UUID of the job posting this task comes from
    protected final UUID jobUuid;
    protected final ParameterMap parameters;

    public TaskInstance(UUID jobUuid, ParameterMap parameters) {
        this.jobUuid = jobUuid;
        this.parameters = parameters;
    }

    public abstract void tick(EntityMinion minion, AIHelper aiHelper);
}
