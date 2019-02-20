package me.dmillerw.droids.api;

import me.dmillerw.droids.common.mesh.AIMeshNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface INetworkComponent {

    public World getWorld();
    public BlockPos getPosition();

    public int getRange();

    public AIMeshNetwork getNetwork();
    public void setNetwork(AIMeshNetwork network);

    /* CLIENT */

    public boolean debugNetworkConnected();
}
