package me.dmillerw.droids.common.util.type;

import java.util.function.Function;

public interface VoidFunction extends Function<Void, Void> {

    public default Void apply(Void aVoid) {
        apply();
        return null;
    }

    public void apply();
}
