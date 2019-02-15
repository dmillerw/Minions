package me.dmillerw.minions.world;

import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.tasks.Job;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class WorldTicker {

    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;

        List<EntityMinion> minions = event.world.getEntities(EntityMinion.class, (minion) -> true);
        List<Job> jobs = WorldJobData.getJobBoard(event.world).getJobs();
    }
}
