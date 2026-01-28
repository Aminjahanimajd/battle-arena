package com.amin.battlearena.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Inventory {
    private final Map<String, Integer> items;
    
    public Inventory() {
        this.items = new HashMap<>();
    }
    
    public void addItem(String itemName) {
        items.put(itemName, items.getOrDefault(itemName, 0) + 1);
    }
    
    public void addItems(String itemName, int quantity) {
        if (quantity > 0) {
            items.put(itemName, items.getOrDefault(itemName, 0) + quantity);
        }
    }
    
    public boolean hasItem(String itemName) {
        return items.getOrDefault(itemName, 0) > 0;
    }
    
    public int getItemCount(String itemName) {
        return items.getOrDefault(itemName, 0);
    }
    
    public void removeItem(String itemName) {
        if (hasItem(itemName)) {
            int count = items.get(itemName);
            if (count > 1) {
                items.put(itemName, count - 1);
            } else {
                items.remove(itemName);
            }
        }
    }
    
    public void clear() {
        items.clear();
    }
    
    public Map<String, Integer> getAllItems() {
        return Collections.unmodifiableMap(items);
    }
    
    public void setItems(Map<String, Integer> newItems) {
        items.clear();
        items.putAll(newItems);
    }
}
