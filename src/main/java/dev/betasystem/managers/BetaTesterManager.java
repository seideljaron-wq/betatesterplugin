package dev.betasystem.managers;

import dev.betasystem.BetaSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BetaTesterManager {

    private final BetaSystem plugin;
    private final List<UUID> testers = new ArrayList<>();

    public BetaTesterManager(BetaSystem plugin) {
        this.plugin = plugin;
        load();
    }

    private void load() {
        testers.clear();
        for (String s : plugin.getConfig().getStringList("beta-testers")) {
            try { testers.add(UUID.fromString(s)); }
            catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Invalid UUID in beta-testers: " + s);
            }
        }
    }

    public boolean isTester(UUID uuid)  { return testers.contains(uuid); }
    public List<UUID> getTesters()      { return new ArrayList<>(testers); }

    public boolean addTester(UUID uuid) {
        if (testers.contains(uuid)) return false;
        testers.add(uuid);
        save();
        return true;
    }

    public boolean removeTester(UUID uuid) {
        boolean removed = testers.remove(uuid);
        if (removed) save();
        return removed;
    }

    private void save() {
        List<String> out = new ArrayList<>();
        testers.forEach(u -> out.add(u.toString()));
        plugin.getConfig().set("beta-testers", out);
        plugin.saveConfig();
    }
}
