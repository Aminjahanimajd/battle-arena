package com.amin.battlearena.economy;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.amin.battlearena.model.Character;

/**
 * Catalog of available upgrades.
 *
 * Some older code constructs StatUpgrade using the simple numeric constructor:
 *   StatUpgrade(int hpPlus, int atkPlus, int defPlus, int rangePlus)
 *
 * Because StatUpgrade is not itself an Upgrade (it doesn't implement Upgrade),
 * we wrap each StatUpgrade into an anonymous Upgrade adapter that delegates
 * apply(...) calls to the StatUpgrade instance and provides id/name/cost.
 */
public final class UpgradeCatalog {
    private final Map<String, Upgrade> map = new LinkedHashMap<>();

    public void register(Upgrade u) { map.put(u.id(), u); }
    public Optional<Upgrade> find(String id) { return Optional.ofNullable(map.get(id)); }
    public Collection<Upgrade> all() { return map.values(); }

    public static UpgradeCatalog defaultCatalog() {
        var cat = new UpgradeCatalog();

        // UP-HP-1: +10 HP cost 50
        {
            final StatUpgrade su = new StatUpgrade(10, 0, 0, 0);
            cat.register(new Upgrade() {
                @Override public String id() { return "UP-HP-1"; }
                @Override public String name() { return "+10 HP"; }
                @Override public int cost() { return 50; }
                @Override public void apply(Character c) { su.apply(c); }
            });
        }

        // UP-ATK-1: +2 Attack cost 60
        {
            final StatUpgrade su = new StatUpgrade(0, 2, 0, 0);
            cat.register(new Upgrade() {
                @Override public String id() { return "UP-ATK-1"; }
                @Override public String name() { return "+2 Attack"; }
                @Override public int cost() { return 60; }
                @Override public void apply(Character c) { su.apply(c); }
            });
        }

        // UP-DEF-1: +2 Defense cost 60
        {
            final StatUpgrade su = new StatUpgrade(0, 0, 2, 0);
            cat.register(new Upgrade() {
                @Override public String id() { return "UP-DEF-1"; }
                @Override public String name() { return "+2 Defense"; }
                @Override public int cost() { return 60; }
                @Override public void apply(Character c) { su.apply(c); }
            });
        }

        return cat;
    }
}
