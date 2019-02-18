package me.dmillerw.minions.entity.ai;

import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;

import java.lang.reflect.Field;
import java.util.Arrays;

public class AIHelper {

    private final EntityMinion minion;
    private final double speed;

    private PathFinder pathFinder;

    public AIHelper(EntityMinion minion, double speed) {
        this.minion = minion;
        this.speed = speed;

        retrievePathFinder();
    }

    private void retrievePathFinder() {
        try {
            Class<PathNavigate> clazz = PathNavigate.class;
            Field fieldPathFinder = Arrays.stream(clazz.getDeclaredFields())
                    .filter((field) -> PathFinder.class == field.getType())
                    .findAny().orElse(null);

            if (fieldPathFinder == null) {
                throw new IllegalStateException("Couldn't find pathFinder field in PathNavigate.class. Contact the mod author!");
            }

            fieldPathFinder.setAccessible(true);
            pathFinder = (PathFinder) fieldPathFinder.get(minion.getNavigator());
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to get path finder from entity navigator: " + ex.toString());
        }
    }

    public boolean canPathTo(BlockPos pos) {
        return getPathToPosition(pos) != null;
    }

    public void moveToPosition(BlockPos pos) {
        boolean moving = minion.getNavigator().setPath(getPathToPosition(pos), speed);
    }

    public void moveToEntity(Entity entity) {
        boolean moving = minion.getNavigator().setPath(getPathToPosition(entity.getPosition()), speed);
    }

    public void finishStep() {

    }

    private Path getPathToPosition(BlockPos targetPos) {
        BlockPos minionPos = minion.getPosition();
        int distance = (int)Math.ceil(minionPos.distanceSq(targetPos));
        PathNavigate navigator = minion.getNavigator();
        minion.world.profiler.startSection("pathfind");
        int i = (int) (distance + 8.0F);
        ChunkCache chunkcache = new ChunkCache(minion.world, minionPos.add(-i, -i, -i), minionPos.add(i, i, i), 0);
        Path path = pathFinder.findPath(chunkcache, minion, targetPos, i);
        minion.world.profiler.endSection();
        return path;
    }
}
