package me.dmillerw.droids.common.block;

import me.dmillerw.droids.common.tile.TileFluidController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockFluidController extends BaseTileBlock {

    public static final String NAME = "fluid_controller";

    protected BlockFluidController() {
        super(NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileFluidController();
    }
}