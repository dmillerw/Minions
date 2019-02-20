package me.dmillerw.droids.common.block;

import me.dmillerw.droids.common.tile.TilePowerController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockPowerController extends BaseTileBlock {

    public static final String NAME = "power_controller";

    protected BlockPowerController() {
        super(NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TilePowerController();
    }
}