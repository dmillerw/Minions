package me.dmillerw.minions.world;

import com.google.common.collect.Maps;
import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.tasks.job.JobPosting;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.Map;
import java.util.UUID;

public class WorldJobData extends WorldSavedData {

    public static WorldJobData getJobBoard(World world) {
        return (WorldJobData) world.loadData(WorldJobData.class, "jobs");
    }

    private final Map<UUID, JobPosting> jobPostings = Maps.newHashMap();

    public WorldJobData() {
        super(ModInfo.ID + ":jobs");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        NBTTagList list = nbt.getTagList("jobs", Constants.NBT.TAG_COMPOUND);
        for (int i=0; i<list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            JobPosting posting = JobPosting.fromNbt(tag);

            jobPostings.put(posting.uuid, posting);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (JobPosting posting : jobPostings.values()) {
            NBTTagCompound tagCompound = new NBTTagCompound();
            posting.writeToNbt(tagCompound);
            list.appendTag(tagCompound);
        }

        compound.setTag("jobs", list);

        return compound;
    }
}
