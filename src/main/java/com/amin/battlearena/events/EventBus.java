package com.amin.battlearena.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Type-safe minimal event bus.
 * subscribe() returns a Runnable that unsubscribes, avoiding unchecked cast warnings.
 */
public final class EventBus {

    private final Map<Class<? extends GameEvent>, List<Consumer<? super GameEvent>>> map = new HashMap<>();

    public <E extends GameEvent> Runnable subscribe(Class<E> type, Consumer<? super E> handler) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(handler);
        List<Consumer<? super GameEvent>> bucket = map.computeIfAbsent(type, k -> new ArrayList<>());
        // Wrap handler to erase type safely
        Consumer<? super GameEvent> wrapper = evt -> {
            if (type.isInstance(evt)) {
                @SuppressWarnings("unchecked")
                E cast = (E) evt;
                ((Consumer<E>) handler).accept(cast);
            }
        };
        bucket.add(wrapper);

        // Return unsubscriber
        return () -> bucket.remove(wrapper);
    }

    public void post(GameEvent evt) {
        if (evt == null) return;
        List<Consumer<? super GameEvent>> bucket = map.get(evt.getClass());
        if (bucket == null) return;
        // iterate on a snapshot to avoid CME if someone unsubscribes during dispatch
        for (Consumer<? super GameEvent> c : List.copyOf(bucket)) {
            c.accept(evt);
        }
    }
}
