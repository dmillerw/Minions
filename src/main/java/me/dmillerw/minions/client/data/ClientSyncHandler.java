package me.dmillerw.minions.client.data;

import com.google.common.collect.Maps;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.network.server.SRegisterStateListener;
import me.dmillerw.minions.network.server.SRequestBatchUpdate;
import me.dmillerw.minions.network.server.SUnregisterStateListener;
import me.dmillerw.minions.tasks.Job;
import me.dmillerw.minions.util.VoidFunction;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ClientSyncHandler {

    public static final ClientSyncHandler INSTANCE = new ClientSyncHandler();

    private Map<UUID, Minion> minionMap = Maps.newHashMap();
    private Map<UUID, Job> jobMap = Maps.newHashMap();

    private VoidFunction stateListener;

    private ClientSyncHandler() {}

    public void requestBatchUpdate() {
        PacketHandler.INSTANCE.sendToServer(new SRequestBatchUpdate());
    }

    public void registerStateListener(VoidFunction stateListener) {
        if (stateListener != null)
            PacketHandler.INSTANCE.sendToServer(new SRegisterStateListener());
        else
            PacketHandler.INSTANCE.sendToServer(new SUnregisterStateListener());

        this.stateListener = stateListener;
    }

    public void handleBatchUpdate(Minion[] minions, Job[] jobs) {
        minionMap.clear();
        jobMap.clear();

        Arrays.stream(minions).forEach((minion) -> minionMap.put(minion.minionId, minion));
        Arrays.stream(jobs).forEach((job) -> jobMap.put(job.uuid, job));

        if (stateListener != null) stateListener.apply(null);
    }

    public Minion[] getMinions() {
        return minionMap.values().toArray(new Minion[0]);
    }

    public Job getJob(UUID jobUuid) {
        return jobMap.get(jobUuid);
    }

    public Job[] getJobs() {
        return jobMap.values().toArray(new Job[0]);
    }

    public void updateMinion(Minion minion) {
        minionMap.put(minion.minionId, minion);
        if (stateListener != null) stateListener.apply(null);
    }

    public void removeMinion(Minion minion) {
        minionMap.remove(minion.minionId);
        if (stateListener != null) stateListener.apply(null);
    }

    public void updateJob(Job job) {
        jobMap.put(job.uuid, job);
        if (stateListener != null) stateListener.apply(null);
    }

    public void removeJob(Job job) {
        jobMap.remove(job.uuid);
        if (stateListener != null) stateListener.apply(null);
    }
}
