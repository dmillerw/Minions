package me.dmillerw.minions.util;

import java.util.Arrays;

public enum MinionType {

    ANYONE, LABORER, FARMER, BUILDER, FORESTER;

    private static MinionType[] values;

    public static MinionType[] getValues() {
        if (values == null) {
            values = MinionType.values();
        }
        return values;
    }

    public static MinionType fromString(String string) {
        return Arrays.stream(getValues()).filter((minion) -> minion.getString().equalsIgnoreCase(string)).findFirst().orElse(LABORER);
    }

    public String getString() {
        return this.name().toLowerCase();
    }
}
