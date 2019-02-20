package me.dmillerw.droids.common.tile;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TileChargingStation extends TileCore {

    private static Map<Integer, Set<BlockPos>> chargingStations = Maps.newHashMap();

    public static void registerChargingStation(TileChargingStation tile) {
        Set<BlockPos> set = chargingStations.get(tile.world.provider.getDimension());
        if (set == null) set = Sets.newHashSet();
        set.add(tile.pos);
        chargingStations.put(tile.world.provider.getDimension(), set);
    }

    public static void removeChargingStation(TileChargingStation tile) {
        Set<BlockPos> set = chargingStations.get(tile.world.provider.getDimension());
        if (set == null) set = Sets.newHashSet();
        set.remove(tile.pos);
        chargingStations.put(tile.world.provider.getDimension(), set);
    }

    public static TileChargingStation getClosestChargingStation(World world, BlockPos position) {
        Set<BlockPos> positions = chargingStations.get(world.provider.getDimension());
        if (positions == null)
            return null;

        BlockPos closest = null;
        double closestDistance = Double.MAX_VALUE;
        for (BlockPos pos : positions) {
            double distance = pos.distanceSq(pos);
            if (distance < closestDistance) {
                closest = pos;
                closestDistance = distance;
            }
        }

        if (closest == null)
            return null;

        return (TileChargingStation) world.getTileEntity(closest);
    }

    private UUID owner;

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            TileChargingStation.registerChargingStation(this);
        }
    }

    @Override
    public void invalidate() {
        if (!world.isRemote) {
            TileChargingStation.removeChargingStation(this);
        }

        super.invalidate();
    }

    @Override
    public void onBlockBreak() {
        if (!world.isRemote) {
            TileChargingStation.removeChargingStation(this);
        }
    }

    /* NBT */

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        super.writeToDisk(compound);

        compound.setLong("owner_most", owner.getMostSignificantBits());
        compound.setLong("owner_least", owner.getLeastSignificantBits());
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        super.readFromDisk(compound);

        owner = new UUID(compound.getLong("owner_most"), compound.getLong("owner_least"));
    }

    /* GET SET */

    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}
