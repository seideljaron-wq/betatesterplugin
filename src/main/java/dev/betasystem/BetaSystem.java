package dev.betasystem;

import dev.betasystem.commands.BetaCommand;
import dev.betasystem.commands.BetaTesterCommand;
import dev.betasystem.commands.BugCommand;
import dev.betasystem.gui.BetaGUIListener;
import dev.betasystem.managers.BetaTesterManager;
import dev.betasystem.managers.DiscordLogger;
import dev.betasystem.managers.FeatureManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BetaSystem extends JavaPlugin {

    private static BetaSystem instance;

    private BetaTesterManager testerManager;
    private FeatureManager featureManager;
    private DiscordLogger discordLogger;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        testerManager  = new BetaTesterManager(this);
        featureManager = new FeatureManager(this);
        discordLogger  = new DiscordLogger(this);

        BetaTesterCommand testerCmd = new BetaTesterCommand(this);
        getCommand("beta-tester").setExecutor(testerCmd);
        getCommand("beta-tester").setTabCompleter(testerCmd);

        getCommand("beta").setExecutor(new BetaCommand(this));
        getCommand("bug").setExecutor(new BugCommand(this));

        getServer().getPluginManager().registerEvents(new BetaGUIListener(this), this);

        getLogger().info("BetaSystem enabled successfully!");
    }

    @Override
    public void onDisable() {
        featureManager.disableAll();
        getLogger().info("BetaSystem disabled.");
    }

    public static BetaSystem getInstance()      { return instance; }
    public BetaTesterManager getTesterManager() { return testerManager; }
    public FeatureManager getFeatureManager()   { return featureManager; }
    public DiscordLogger getDiscordLogger()     { return discordLogger; }

    public boolean isAllowed(org.bukkit.entity.Player player) {
        return player.isOp()
            || player.hasPermission("beta.admin")
            || testerManager.isTester(player.getUniqueId());
    }
}
