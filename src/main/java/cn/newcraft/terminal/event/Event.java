package cn.newcraft.terminal.event;

import cn.newcraft.terminal.plugin.Plugin;
import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;

public class Event {

    private static HashMap<Plugin, List<Listener>> listeners = new HashMap<>();

    public static HashMap<Plugin, List<Listener>> getListeners() {
        return listeners;
    }

    public static void regListener(Plugin plugin, Listener listener) {
        List<Listener> list;
        if (listeners.get(plugin) != null) {
            list = listeners.get(plugin);
        } else {
            list = Lists.newArrayList();
        }
        list.add(listener);
        listeners.put(plugin, list);
    }

    public static void callEvent(Event event) throws InvocationTargetException, IllegalAccessException {
        for (List<Listener> listeners : listeners.values()) {
            for (Listener listener : listeners) {
                for (Method m : listener.getClass().getMethods()) {
                    if (m.isAnnotationPresent(SubscribeEvent.class)) {
                        for (Parameter parameter : m.getParameters()) {
                            if (parameter.getType() == event.getClass()) {
                                m.invoke(listener, event);
                            }
                        }
                    }
                }
            }
        }
    }
}
