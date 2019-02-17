package me.dmillerw.minions.client.handler;

import me.dmillerw.minions.util.Area;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;
import java.util.function.BiConsumer;

public class CoordSelectionHandler {

    public static final CoordSelectionHandler INSTANCE = new CoordSelectionHandler();

    // -1: not selecting, 0: selecting area, 1: selecting position
    private int coordSelection;

    private BlockPos areaStart;
    private BlockPos areaEnd;

    private BlockPos blockPos;

    private UUID jobUuid;
    private BiConsumer callback;

    public boolean isSelecting() {
        return jobUuid != null && coordSelection >= 0;
    }

    public void startBlockPosSelection(UUID jobUuid, BiConsumer<UUID, BlockPos> callback) {
        coordSelection = 1;
        this.jobUuid = jobUuid;
        this.callback = callback;
    }

    public void startAreaSelection(UUID jobUuid, BiConsumer<UUID, Area> callback) {
        coordSelection = 0;
        this.jobUuid = jobUuid;
        this.callback = callback;
    }

    public void clearSelection() {
        coordSelection = -1;

        jobUuid = null;
        this.callback = null;

        areaStart = null;
        areaEnd = null;
        blockPos = null;
    }

    public void selectCoordinate(BlockPos position) {
        if (coordSelection == -1)
            return;

        if (coordSelection == 0) {
            if (areaStart == null) {
                areaStart = position;
                return;
            } else {
                areaEnd = position;
                if (callback != null) {
                    callback.accept(jobUuid, new Area(areaStart, areaEnd));
                }

                clearSelection();

                return;
            }
        }

        if (coordSelection == 1) {
            blockPos = position;
            if (callback != null) {
                callback.accept(jobUuid, blockPos);
            }

            clearSelection();

            return;
        }
    }
}
