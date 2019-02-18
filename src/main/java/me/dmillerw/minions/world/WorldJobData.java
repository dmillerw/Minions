package me.dmillerw.minions.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.network.ServerSyncHandler;
import me.dmillerw.minions.tasks.Job;
import me.dmillerw.minions.tasks.JobState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class WorldJobData extends WorldSavedData {

    public static WorldJobData getJobBoard(World world) {
        int dimension = world.provider.getDimension();
        world = FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension);
        WorldJobData data = (WorldJobData) world.loadData(WorldJobData.class, "jobs");
        if (data == null) {
            data = new WorldJobData();
            world.setData("jobs", data);
        }
        return data;
    }

    public static void updateJobState(EntityMinion minion, UUID jobUuid, JobState state) {
//        WorldJobData data = getJobBoard(minion.world);
//        data.getJob(jobUuid).updateState(minion, state);
    }

    private final Map<UUID, Job> uuidToJobMap = Maps.newHashMap();
    private final LinkedList<Job> sortedJobs = Lists.newLinkedList();

    public WorldJobData() {
        super(ModInfo.ID + ":jobs");
    }

    public void addJob(Job posting) {
        boolean modify = uuidToJobMap.containsKey(posting.uuid);

        uuidToJobMap.put(posting.uuid, posting);

        sortedJobs.clear();
        sortedJobs.addAll(uuidToJobMap.values());

        sortedJobs.sort(Comparator.comparingInt(p -> p.priority));

        if (modify)
            ServerSyncHandler.INSTANCE.updateJob(posting);
        else
            ServerSyncHandler.INSTANCE.addJob(posting);
    }

    public void deleteJob(UUID targetUuid) {
        Job job = uuidToJobMap.get(targetUuid);
        if (job == null)
            return;

        uuidToJobMap.remove(targetUuid);
        sortedJobs.clear();
        sortedJobs.addAll(uuidToJobMap.values());

        sortedJobs.sort(Comparator.comparingInt(p -> p.priority));

        ServerSyncHandler.INSTANCE.removeJob(job);
    }

    public Job getJob(UUID uuid) {
        return uuidToJobMap.get(uuid);
    }

    public ImmutableList<Job> getJobs() {
        return ImmutableList.copyOf(sortedJobs);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("jobs", Constants.NBT.TAG_COMPOUND);
        for (int i=0; i<list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            Job posting = Job.fromNbt(tag);

            addJob(posting);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (Job posting : uuidToJobMap.values()) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            posting.writeToNbt(tagCompound);
            list.appendTag(tagCompound);
        }

        compound.setTag("jobs", list);

        return compound;
    }
}
