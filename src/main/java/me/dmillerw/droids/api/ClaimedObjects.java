package me.dmillerw.droids.api;

import com.google.common.collect.Sets;
import me.dmillerw.droids.common.network.PacketHandler;
import me.dmillerw.droids.common.network.packets.CClaimDebug;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class ClaimedObjects {

    private static class EntityKey {

        private final UUID uuid;
        private final Entity entity;

        private EntityKey(Entity entity) {
            this.uuid = entity.getPersistentID();
            this.entity = entity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EntityKey entityKey = (EntityKey) o;
            return Objects.equals(uuid, entityKey.uuid);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uuid);
        }
    }

    private final World world;

    private Set<EntityKey> claimedEntities = Sets.newHashSet();
    private Set<BlockPos> claimedBlocks = Sets.newHashSet();

    public ClaimedObjects(World world) {
        this.world = world;
    }

    public boolean claimEntity(Entity entity) {
        final EntityKey key = new EntityKey(entity);
        if (claimedEntities.contains(key))
            return false;

        claimedEntities.add(key);

        // TODO: not here...
        PacketHandler.INSTANCE.sendToDimension(new CClaimDebug(CClaimDebug.CLAIM, entity.getPersistentID()), entity.world.provider.getDimension());

        return true;
    }

    public void releaseEntity(Entity entity) {
        PacketHandler.INSTANCE.sendToDimension(new CClaimDebug(CClaimDebug.RELEASE, entity.getPersistentID()), entity.world.provider.getDimension());

        final EntityKey key = new EntityKey(entity);
        claimedBlocks.remove(key);
    }

    public boolean claimBlock(BlockPos position) {
        if (claimedBlocks.contains(position))
            return false;

        claimedBlocks.add(position);

        PacketHandler.INSTANCE.sendToDimension(new CClaimDebug(CClaimDebug.CLAIM, position), world.provider.getDimension());

        return true;
    }

    public void releaseBlock(BlockPos position) {
        PacketHandler.INSTANCE.sendToDimension(new CClaimDebug(CClaimDebug.RELEASE, position), world.provider.getDimension());

        claimedBlocks.remove(position);
    }
}
