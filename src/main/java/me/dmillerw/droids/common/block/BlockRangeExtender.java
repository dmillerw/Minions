package me.dmillerw.droids.common.block;

import me.dmillerw.droids.common.tile.TileRangeExtender;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockRangeExtender extends BaseTileBlock {

    public static final String NAME = "range_extender";

    protected BlockRangeExtender() {
        super(NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileRangeExtender();
    }
}