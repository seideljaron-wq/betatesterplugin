package dev.betasystem.gui;

import dev.betasystem.BetaSystem;
import dev.betasystem.managers.FeatureManager.Feature;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BetaGUIListener implements Listener {

    private final BetaSystem plugin;

    public BetaGUIListener(BetaSystem plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = PlainTextComponentSerializer.plainText()
            .serialize(event.getView().title());

        if (!title.equals(BetaGUI.TITLE)) return;

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= 36) return;

        // Check if clicked slot is a feature slot
        int[] slots = BetaGUI.SLOTS;
        for (int i = 0; i < slots.length; i++) {
            if (slots[i] != slot) continue;

            Feature  feature  = BetaGUI.FEATURES[i];
            Material mat      = BetaGUI.MATERIALS[i];

            // Toggle the feature
            boolean nowOn = plugin.getFeatureManager().toggle(player, feature);

            // Update item in place (no need to rebuild whole GUI)
            event.getInventory().setItem(slot, BetaGUI.buildItem(feature, mat, nowOn));

            // Discord log
            plugin.getDiscordLogger().logFeatureToggle(
                player.getName(), feature.label, nowOn);

            return;
        }
        // Clicked glass – ignore
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Clean up all active features when player leaves
        plugin.getFeatureManager().disablePlayer(event.getPlayer());
    }
}
