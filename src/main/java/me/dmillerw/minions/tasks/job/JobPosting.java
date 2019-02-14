package me.dmillerw.minions.tasks.job;

import com.google.common.collect.Maps;
import me.dmillerw.minions.tasks.ParameterMap;
import me.dmillerw.minions.tasks.TaskDefinition;
import me.dmillerw.minions.tasks.TaskRegistry;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Map;
import java.util.UUID;

public class JobPosting {

    public static JobPosting fromNbt(NBTTagCompound tag) {
        TaskDefinition task = TaskRegistry.getTask(tag.getString("task"));
        UUID uuid = new UUID(tag.getLong("most"), tag.getLong("least"));
        ParameterMap parameters = ParameterMap.fromNbt(task, tag.getCompoundTag("parameters"));

        return new JobPosting(task, uuid, parameters);
    }

    public final TaskDefinition task;
    public final UUID uuid;
    public final ParameterMap parameters;

    private final Map<UUID, JobState> jobState = Maps.newHashMap();

    public JobPosting(TaskDefinition task, ParameterMap parameters) {
        this.task = task;
        this.uuid = UUID.randomUUID();
        this.parameters = parameters;
    }

    public JobPosting(TaskDefinition task, UUID uuid, ParameterMap parameters) {
        this.task = task;
        this.uuid = uuid;
        this.parameters = parameters;
    }

    public void writeToNbt(NBTTagCompound tag) {
        tag.setString("task", task.id);
        tag.setLong("most", uuid.getMostSignificantBits());
        tag.setLong("least", uuid.getLeastSignificantBits());

        NBTTagCompound params = new NBTTagCompound();
        parameters.writeToNbt(params);

        tag.setTag("parameters", params);
    }
}
