package dev.betasystem.commands;

import dev.betasystem.BetaSystem;
import dev.betasystem.gui.BetaGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BetaCommand implements CommandExecutor {

    private final BetaSystem plugin;

    public BetaCommand(BetaSystem plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if (!plugin.isAllowed(player)) {
            player.sendMessage(Component.text(
                "✗ You are not a Beta Tester.", NamedTextColor.RED));
            return true;
        }

        player.openInventory(new BetaGUI(plugin).build(player));
        return true;
    }
}
