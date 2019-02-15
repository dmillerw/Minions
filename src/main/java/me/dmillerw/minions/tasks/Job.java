package me.dmillerw.minions.tasks;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.netty.buffer.ByteBuf;
import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.util.MinionType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A job is a configured defined instance of a code-driven Task
 * It has been created in-world by the player
 */
public class Job {

    public static Job fromBuffer(ByteBuf buffer) {
        UUID uuid = new UUID(buffer.readLong(), buffer.readLong());
        String title = ByteBufUtils.readUTF8String(buffer);
        TaskDefinition task = TaskRegistry.getTask(ByteBufUtils.readUTF8String(buffer));
        MinionType type = MinionType.fromString(ByteBufUtils.readUTF8String(buffer));
        ParameterMap parameters = ParameterMap.fromBuffer(task, buffer);
        int priority = buffer.readInt();

        return new Job(uuid, title, task, type, priority, parameters);
    }

    public static Job fromNbt(NBTTagCompound tag) {
        UUID uuid = new UUID(tag.getLong("most"), tag.getLong("least"));
        String title = tag.getString("title");
        TaskDefinition task = TaskRegistry.getTask(tag.getString("task"));
        MinionType type = MinionType.fromString(tag.getString("minionType"));
        int priority = tag.getInteger("priority");
        ParameterMap parameters = ParameterMap.fromNbt(task, tag.getCompoundTag("parameters"));

        return new Job(uuid, title, task, type, priority, parameters);
    }

    public final UUID uuid;
    public final String title;
    public final TaskDefinition task;
    public final MinionType type;
    public final int priority;
    public final ParameterMap parameters;

    // Map of a minion's uuid, to its state
    private final Map<UUID, JobState> jobState = Maps.newHashMap();
    private final Set<UUID> stateListeners = Sets.newHashSet();

    public Job(String title, TaskDefinition task, MinionType type, ParameterMap parameters) {
        this(UUID.randomUUID(), title, task, type, 0, parameters);
    }

    public Job(UUID uuid, String title, TaskDefinition task, MinionType type, int priority, ParameterMap parameters) {
        this.uuid = uuid;
        this.title = title;
        this.task = task;
        this.type = type;
        this.parameters = parameters;
        this.priority = priority;
    }

    public void updateState(EntityMinion minion, JobState state) {
        jobState.put(minion.getUniqueID(), state);
    }

    public void writeToBuffer(ByteBuf buffer) {
        buffer.writeLong(uuid.getMostSignificantBits());
        buffer.writeLong(uuid.getLeastSignificantBits());
        ByteBufUtils.writeUTF8String(buffer, title);
        ByteBufUtils.writeUTF8String(buffer, task.id);
        ByteBufUtils.writeUTF8String(buffer, type.getString());
        buffer.writeInt(priority);
        parameters.writeToBuffer(buffer);
    }

    public void writeToNbt(NBTTagCompound tag) {
        tag.setLong("most", uuid.getMostSignificantBits());
        tag.setLong("least", uuid.getLeastSignificantBits());

        tag.setString("title", title);

        tag.setString("task", task.id);

        tag.setString("minionType", type.getString());

        NBTTagCompound params = new NBTTagCompound();
        parameters.writeToNbt(params);
        tag.setTag("parameters", params);

        tag.setInteger("priority", priority);
    }
}
