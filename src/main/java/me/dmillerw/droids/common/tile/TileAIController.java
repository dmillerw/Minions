package me.dmillerw.droids.common.tile;

import me.dmillerw.droids.api.INetworkComponent;
import me.dmillerw.droids.api.IPlayerOwned;
import me.dmillerw.droids.common.Constants;
import me.dmillerw.droids.common.mesh.ActionNetwork;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class TileAIController extends TileCore implements INetworkComponent, IPlayerOwned {

    private UUID owner;
    private ActionNetwork network;

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            this.network = new ActionNetwork(this);
            ActionNetwork.registerNetwork(world, network);
        }
    }

    @Override
    public void invalidate() {
        if (!world.isRemote) {
            ActionNetwork.destroyNetwork(world, network);
        }

        super.invalidate();
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    public int getRange() {
        return Constants.AI_CONTROLLER_DEFAULT_RANGE;
    }

    @Override
    public ActionNetwork getNetwork() {
        return this.network;
    }

    @Override
    public void setNetwork(ActionNetwork network) {}

    @Override
    public boolean debugNetworkConnected() {
        return true;
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

    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }
}
