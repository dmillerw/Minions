package me.dmillerw.droids.common.block;

import me.dmillerw.droids.common.tile.TileZoneController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockZoneController extends BaseTileBlock {

    public static final String NAME = "zone_controller";

    protected BlockZoneController() {
        super(NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileZoneController();
    }
}