package me.dmillerw.minions.event;

import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.network.ServerSyncHandler;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityEventHandler {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof EntityMinion)) return;
        if (event.getWorld().isRemote) return;
        ServerSyncHandler.INSTANCE.addMinion((EntityMinion) event.getEntity());
    }

    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof EntityMinion)) return;
        if (event.getEntity().world.isRemote) return;
        ServerSyncHandler.INSTANCE.removeMinion((EntityMinion) event.getEntity());
    }
}
