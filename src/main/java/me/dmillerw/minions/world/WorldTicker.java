package me.dmillerw.minions.world;

import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.tasks.Job;
import me.dmillerw.minions.tasks.TaskStep;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class WorldTicker {

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;

        if (event.world.isRemote)
            return;

        WorldJobData data = WorldJobData.getJobBoard(event.world);
        List<Job> jobs = data.getJobs();

        jobs.forEach((j) -> j.tick(event.world));

        event.world.setData("jobs", data);
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        if (!(event.getEntity() instanceof EntityMinion))
            return;

        EntityMinion minion = (EntityMinion) event.getEntityLiving();
        WorldJobData data = WorldJobData.getJobBoard(minion.world);

        if (minion.activeTaskStep == null) {
            Job job = data.getJobs().stream()
                    .filter((j) -> j.canMinionPerform(minion))
                    .findAny().orElse(null);

            if (job != null) {
                TaskStep step = job.getStep();
                if (step != null) {
                    minion.activeTaskStep = step;
                    minion.activeTaskStep.setClaimed(true);
                }
            }
        }
    }
}
