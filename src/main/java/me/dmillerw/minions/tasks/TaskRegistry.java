package me.dmillerw.minions.tasks;

import com.google.common.collect.Maps;

import java.util.Map;

public class TaskRegistry {

    public static final String COLLECT_ITEMS = "collect_items";

    private static Map<String, TaskDefinition> registeredTasks = Maps.newHashMap();

    public static void registerTask(TaskDefinition definition) {
        registeredTasks.put(definition.id, definition);
    }

    public static TaskDefinition getTask(String key) {
        return registeredTasks.get(key);
    }
}
