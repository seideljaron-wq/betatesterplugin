package dev.betasystem.gui;

import dev.betasystem.BetaSystem;
import dev.betasystem.managers.FeatureManager.Feature;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BetaGUI {

    public static final String TITLE = "✦ Beta Features ✦";

    // 4 rows (36 slots)
    // Row 2 center (slots 10-16): feature items
    //   10 = Spectator
    //   12 = Vanish
    //   13 = Speed (center)
    //   14 = Night Vision
    //   16 = No Fall
    // All other slots = gray glass filler

    public static final int[]    SLOTS    = { 10, 12, 13, 14, 16 };
    public static final Feature[] FEATURES = {
        Feature.SPECTATOR,
        Feature.VANISH,
        Feature.SPEED,
        Feature.NIGHT_VISION,
        Feature.NO_FALL
    };
    public static final Material[] MATERIALS = {
        Material.ENDER_EYE,
        Material.GRAY_DYE,
        Material.SUGAR,
        Material.GOLDEN_CARROT,
        Material.FEATHER
    };

    private final BetaSystem plugin;

    public BetaGUI(BetaSystem plugin) { this.plugin = plugin; }

    public Inventory build(Player player) {
        Inventory inv = Bukkit.createInventory(null, 36,
            Component.text(TITLE, NamedTextColor.LIGHT_PURPLE)
                .decorate(TextDecoration.BOLD));

        // Fill everything with gray glass
        ItemStack glass = glass();
        for (int i = 0; i < 36; i++) inv.setItem(i, glass);

        // Place feature items
        for (int i = 0; i < SLOTS.length; i++) {
            boolean on = plugin.getFeatureManager()
                .isActive(player.getUniqueId(), FEATURES[i]);
            inv.setItem(SLOTS[i], buildItem(FEATURES[i], MATERIALS[i], on));
        }

        return inv;
    }

    public static ItemStack buildItem(Feature feature, Material mat, boolean active) {
        ItemStack item = new ItemStack(mat);
        ItemMeta  meta = item.getItemMeta();

        // Name: icon + label, green if on, white if off
        meta.displayName(
            Component.text(feature.icon + " " + feature.label,
                active ? NamedTextColor.GREEN : NamedTextColor.WHITE)
                .decoration(TextDecoration.ITALIC, false)
                .decorate(TextDecoration.BOLD)
        );

        // Lore: status
        meta.lore(List.of(
            Component.empty(),
            Component.text("Status: ", NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false)
                .append(
                    Component.text(active ? "ACTIVATED" : "DEACTIVATED",
                        active ? NamedTextColor.GREEN : NamedTextColor.RED)
                        .decoration(TextDecoration.ITALIC, false)
                        .decorate(TextDecoration.BOLD)
                ),
            Component.empty(),
            Component.text(active ? "Click to deactivate." : "Click to activate.",
                NamedTextColor.DARK_GRAY)
                .decoration(TextDecoration.ITALIC, false)
        ));

        item.setItemMeta(meta);
        return item;
    }

    private ItemStack glass() {
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta  meta = pane.getItemMeta();
        meta.displayName(Component.empty());
        pane.setItemMeta(meta);
        return pane;
    }
}
