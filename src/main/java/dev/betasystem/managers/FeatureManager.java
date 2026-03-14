package dev.betasystem.managers;

import dev.betasystem.BetaSystem;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class FeatureManager {

    private final BetaSystem plugin;

    // UUID -> set of active features
    private final Map<UUID, Set<Feature>> active = new HashMap<>();

    public enum Feature {
        SPECTATOR  ("Spectator Mode", "🔭", "ENDER_EYE"),
        VANISH     ("Vanish",         "👻", "GRAY_DYE"),
        SPEED      ("Speed Boost",    "⚡", "SUGAR"),
        NIGHT_VISION("Night Vision",  "🌙", "GOLDEN_CARROT"),
        NO_FALL    ("No Fall Damage", "🛡", "FEATHER");

        public final String label;
        public final String icon;
        public final String material;

        Feature(String label, String icon, String material) {
            this.label    = label;
            this.icon     = icon;
            this.material = material;
        }
    }

    public FeatureManager(BetaSystem plugin) {
        this.plugin = plugin;
    }

    public boolean isActive(UUID uuid, Feature f) {
        return active.getOrDefault(uuid, Set.of()).contains(f);
    }

    /** Toggle feature. Returns true = now ON, false = now OFF */
    public boolean toggle(Player player, Feature f) {
        Set<Feature> set = active.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        if (set.contains(f)) {
            set.remove(f);
            disable(player, f);
            return false;
        } else {
            set.add(f);
            enable(player, f);
            return true;
        }
    }

    private void enable(Player player, Feature f) {
        switch (f) {
            case SPECTATOR -> {
                player.setMetadata("beta_gm",
                    new FixedMetadataValue(plugin, player.getGameMode().name()));
                player.setGameMode(GameMode.SPECTATOR);
            }
            case VANISH -> {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    if (!other.getUniqueId().equals(player.getUniqueId())) {
                        other.hidePlayer(plugin, player);
                    }
                }
            }
            case SPEED ->
                player.addPotionEffect(new PotionEffect(
                    PotionEffectType.SPEED, Integer.MAX_VALUE, 3, false, false, true));
            case NIGHT_VISION ->
                player.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false, true));
            case NO_FALL ->
                player.setFallDistance(0f);
        }
    }

    private void disable(Player player, Feature f) {
        switch (f) {
            case SPECTATOR -> {
                GameMode gm = GameMode.SURVIVAL;
                if (player.hasMetadata("beta_gm")) {
                    try { gm = GameMode.valueOf(player.getMetadata("beta_gm").get(0).asString()); }
                    catch (Exception ignored) {}
                    player.removeMetadata("beta_gm", plugin);
                }
                player.setGameMode(gm);
            }
            case VANISH -> {
                for (Player other : Bukkit.getOnlinePlayers()) {
                    other.showPlayer(plugin, player);
                }
            }
            case SPEED ->
                player.removePotionEffect(PotionEffectType.SPEED);
            case NIGHT_VISION ->
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            case NO_FALL -> {}
        }
    }

    public void disablePlayer(Player player) {
        Set<Feature> set = active.remove(player.getUniqueId());
        if (set == null) return;
        for (Feature f : set) disable(player, f);
    }

    public void disableAll() {
        for (UUID uuid : active.keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) {
                for (Feature f : active.get(uuid)) disable(p, f);
            }
        }
        active.clear();
    }
}
