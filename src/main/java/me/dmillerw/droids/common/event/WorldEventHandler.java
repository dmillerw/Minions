package me.dmillerw.droids.common.event;

import com.google.common.collect.Maps;
import me.dmillerw.droids.api.action.Action;
import me.dmillerw.droids.common.helper.DroidTracker;
import me.dmillerw.droids.common.mesh.AIMeshNetwork;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class WorldEventHandler {

    private static Map<Integer, Map<UUID, Action>> runningActions = Maps.newHashMap();

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;

        if (event.world.isRemote)
            return;

        Collection<AIMeshNetwork> networks = AIMeshNetwork.getAllNetworks(event.world);
        networks.forEach(AIMeshNetwork::tick);
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        DroidTracker.clearDimension(event.getWorld().provider.getDimension());
    }
}
