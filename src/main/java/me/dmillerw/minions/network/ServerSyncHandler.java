package me.dmillerw.minions.network;

import com.google.common.collect.Sets;
import me.dmillerw.minions.client.data.Minion;
import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.network.client.CStateUpdate;
import me.dmillerw.minions.tasks.Job;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Set;
import java.util.UUID;

public class ServerSyncHandler {

    public static final ServerSyncHandler INSTANCE = new ServerSyncHandler();

    private Set<UUID> activeListeners = Sets.newHashSet();

    public void registerListener(UUID uuid) {
        this.activeListeners.add(uuid);
    }

    public void unregisterListener(UUID uuid) {
        this.activeListeners.remove(uuid);
    }

    private void sendPacket(IMessage packet) {
        FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().stream()
                .filter((player) -> activeListeners.contains(player.getGameProfile().getId()))
                .forEach((player) -> PacketHandler.INSTANCE.sendTo(packet, player));
    }

    public void addJob(Job job) {
        CStateUpdate.JobUpdate packet = new CStateUpdate.JobUpdate();
        packet.type = CStateUpdate.Type.ADD;
        packet.job = job;

        sendPacket(packet);
    }

    public void updateJob(Job job) {
        CStateUpdate.JobUpdate packet = new CStateUpdate.JobUpdate();
        packet.type = CStateUpdate.Type.MODIFY;
        packet.job = job;

        sendPacket(packet);
    }

    public void removeJob(Job job) {
        CStateUpdate.JobUpdate packet = new CStateUpdate.JobUpdate();
        packet.type = CStateUpdate.Type.REMOVE;
        packet.job = job;

        sendPacket(packet);
    }

    public void addMinion(EntityMinion minion) {
        CStateUpdate.MinionUpdate packet = new CStateUpdate.MinionUpdate();
        packet.type = CStateUpdate.Type.ADD;
        packet.minion = new Minion(minion);

        sendPacket(packet);
    }

    public void updateMinion(EntityMinion minion) {
        CStateUpdate.MinionUpdate packet = new CStateUpdate.MinionUpdate();
        packet.type = CStateUpdate.Type.MODIFY;
        packet.minion = new Minion(minion);

        sendPacket(packet);
    }

    public void removeMinion(EntityMinion minion) {
        CStateUpdate.MinionUpdate packet = new CStateUpdate.MinionUpdate();
        packet.type = CStateUpdate.Type.REMOVE;
        packet.minion = new Minion(minion);

        sendPacket(packet);
    }
}
