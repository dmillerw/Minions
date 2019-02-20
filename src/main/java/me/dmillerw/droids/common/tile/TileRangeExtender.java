package me.dmillerw.droids.common.tile;

import me.dmillerw.droids.common.Constants;

public class TileRangeExtender extends TileBaseComponent {

    @Override
    public int getRange() {
        return Constants.RANGE_EXTENDER_DEFAULT_RANGE;
    }
}
