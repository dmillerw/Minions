package me.dmillerw.droids.api.action;

import com.google.common.collect.Maps;
import me.dmillerw.droids.api.IActionProvider;

import java.util.Map;
import java.util.function.Function;

public class ActionFactory {

    private static Map<String, Function<IActionProvider, Action>> registeredActions = Maps.newHashMap();

    static {
        registeredActions.put(ActionPickupItem.KEY, ActionPickupItem::new);
        registeredActions.put(ActionMove.KEY, ActionMove::new);
    }

    public static Action getAction(IActionProvider provider, String key) {
        return registeredActions.get(key).apply(provider);
    }
}
