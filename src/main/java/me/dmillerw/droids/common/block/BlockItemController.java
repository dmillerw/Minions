package me.dmillerw.droids.common.block;

import me.dmillerw.droids.common.tile.TileItemController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockItemController extends BaseTileBlock {

    public static final String NAME = "item_controller";

    protected BlockItemController() {
        super(NAME);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileItemController();
    }
}