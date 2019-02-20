package me.dmillerw.droids.api.action;

import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class ActionState {

    public static enum Progress {

        INIITIAL, IN_PROGRESS, COMPLETE, CANCELLED
    }

    public static ActionState fromNbt(NBTTagCompound tag) {
        UUID droidId = new UUID(tag.getLong("most"), tag.getLong("least"));
        Progress progress = Progress.values()[tag.getInteger("progress")];

        return new ActionState(droidId, progress);
    }

    public static final ActionState INITIAL_STATE = new ActionState(null, Progress.INIITIAL);

    public final UUID droidId;
    public final Progress progress;

    public ActionState(UUID droidId, Progress progress) {
        this.droidId = droidId;
        this.progress = progress;
    }

    public void writeToNbt(NBTTagCompound tag) {
        tag.setLong("most", droidId.getMostSignificantBits());
        tag.setLong("least", droidId.getLeastSignificantBits());
        tag.setInteger("progress", progress.ordinal());
    }

    public ActionState progress(Progress progress) {
        return new ActionState(this.droidId, progress);
    }
}
