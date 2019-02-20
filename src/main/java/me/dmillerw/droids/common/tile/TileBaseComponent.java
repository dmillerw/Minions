package me.dmillerw.droids.common.tile;

import me.dmillerw.droids.api.INetworkComponent;
import me.dmillerw.droids.api.IPlayerOwned;
import me.dmillerw.droids.common.mesh.AIMeshNetwork;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class TileBaseComponent extends TileCore implements INetworkComponent, IPlayerOwned {

    protected static final String KEY_RANGE = "range";
    protected static final String KEY_CONNECTED = "connected";

    private UUID owner;
    protected AIMeshNetwork network;
    private int range = 0;

    public boolean clientIsNetworkConnected = false;

    @Override
    public void onLoad() {
        if (!world.isRemote) {
            AIMeshNetwork.addToNetwork(this);
        }
    }

    @Override
    public void onBlockBreak() {
        if (!world.isRemote) {
            this.tileEntityInvalid = true;
            AIMeshNetwork.removeFromNetwork(this);
        }
    }

    @Override
    public void invalidate() {
        if (!world.isRemote) {
            AIMeshNetwork.removeFromNetwork(this);
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

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public AIMeshNetwork getNetwork() {
        return this.network;
    }

    @Override
    public void setNetwork(AIMeshNetwork network) {
        this.network = network;

        markDirtyAndNotify();
    }

    /* NBT */

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        super.writeToDisk(compound);

        compound.setLong("owner_most", owner.getMostSignificantBits());
        compound.setLong("owner_least", owner.getLeastSignificantBits());

        compound.setInteger(KEY_RANGE, range);
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        super.readFromDisk(compound);

        owner = new UUID(compound.getLong("owner_most"), compound.getLong("owner_least"));
        range = compound.getInteger(KEY_RANGE);
    }

    @Override
    public void writeDescription(NBTTagCompound compound) {
        super.writeDescription(compound);

        compound.setInteger(KEY_RANGE, range);
        compound.setBoolean(KEY_CONNECTED, network != null);
    }

    @Override
    public void readDescription(NBTTagCompound compound) {
        super.readDescription(compound);

        range = compound.getInteger(KEY_RANGE);
        clientIsNetworkConnected = compound.getBoolean(KEY_CONNECTED);
    }

    @Override
    public boolean debugNetworkConnected() {
        return clientIsNetworkConnected;
    }

    /* UTIL */

    private void setRange(int range) {
        this.range = range;

        markDirtyAndNotify();
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}
