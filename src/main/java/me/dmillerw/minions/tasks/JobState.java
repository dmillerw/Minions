package me.dmillerw.minions.tasks;

public class JobState {

    public static enum MinionState {

        IDLE, MOVING, CANT_PATH, WORKING
    }

    public final MinionState minionState;
    public final String stateDescription;

    public JobState(MinionState minionState) {
        this(minionState, "");
    }

    public JobState(MinionState minionState, String stateDescription) {
        this.minionState = minionState;
        this.stateDescription = stateDescription;
    }
}
