package dev.betasystem.commands;

import dev.betasystem.BetaSystem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BugCommand implements CommandExecutor {

    private final BetaSystem plugin;

    public BugCommand(BetaSystem plugin) { this.plugin = plugin; }

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

        if (args.length == 0) {
            player.sendMessage(Component.text(
                "Usage: /bug <description>", NamedTextColor.YELLOW));
            return true;
        }

        String reason = String.join(" ", args);

        player.sendMessage(
            Component.text("✔ Bug report submitted! ", NamedTextColor.GREEN)
                .append(Component.text("Thank you for helping improve RuneMC.",
                    NamedTextColor.GRAY)));

        plugin.getDiscordLogger().logBug(player.getName(), reason);
        return true;
    }
}
