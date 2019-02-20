package me.dmillerw.droids.api;

import me.dmillerw.droids.common.mesh.ActionNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface INetworkComponent {

    public World getWorld();
    public BlockPos getPosition();

    public int getRange();

    public ActionNetwork getNetwork();
    public void setNetwork(ActionNetwork network);

    /* CLIENT */

    public boolean debugNetworkConnected();
}
