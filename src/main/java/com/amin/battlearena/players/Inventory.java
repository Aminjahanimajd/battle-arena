package com.amin.battlearena.players;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.amin.battlearena.domain.items.Consumable;

// Simple per-player inventory for consumable items used in battle
public final class Inventory {
    private final List<Consumable> items = new ArrayList<>();

    public List<Consumable> getItems() { return Collections.unmodifiableList(items); }

    public void add(Consumable item) { if (item != null) items.add(item); }

    public boolean remove(Consumable item) { return items.remove(item); }

    public boolean isEmpty() { return items.isEmpty(); }
}