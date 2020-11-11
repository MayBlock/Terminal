package cn.newcraft.terminal.event;

import cn.newcraft.terminal.plugin.Plugin;
import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public static Plugin getPluginFromListener(Listener listener) {
        for (Map.Entry<Plugin, List<Listener>> entry : listeners.entrySet()) {
            if (entry.getValue().contains(listener)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void callEvent(Event event) throws InvocationTargetException, IllegalAccessException {
        for (Listener listener : listeners.values().stream().flatMap(Collection::stream).collect(Collectors.toList())) {
            for (Method m : listener.getClass().getMethods()) {
                if (m.getAnnotation(SubscribeEvent.class) != null) {
                    final Class<? extends Event> eventClass = m.getParameterTypes()[0].asSubclass(Event.class);
                    if (m.getParameterTypes().length == 1 && eventClass.isAssignableFrom(event.getClass())) {
                        m.invoke(listener, event);
                    }
                }
            }
        }
    }
}
