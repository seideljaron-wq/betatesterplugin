package dev.betasystem.managers;

import dev.betasystem.BetaSystem;
import org.bukkit.Bukkit;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class DiscordLogger {

    private final BetaSystem plugin;

    private static final int GREEN  = 0x57F287;
    private static final int RED    = 0xED4245;
    private static final int YELLOW = 0xFEE75C;
    private static final int BLUE   = 0x5865F2;

    public DiscordLogger(BetaSystem plugin) { this.plugin = plugin; }

    public void logFeatureToggle(String player, String feature, boolean on) {
        String url = plugin.getConfig().getString("webhook-features", "");
        sendEmbed(url,
            on ? GREEN : RED,
            (on ? "✅" : "❌") + " Feature " + (on ? "Activated" : "Deactivated"),
            "**" + player + "** " + (on ? "enabled" : "disabled") + " **" + feature + "**",
            "Status", on ? "ACTIVATED" : "DEACTIVATED"
        );
    }

    public void logBug(String player, String reason) {
        String url = plugin.getConfig().getString("webhook-bugs", "");
        sendEmbed(url,
            YELLOW,
            "🐛 Bug Report",
            "**" + player + "** submitted a bug report",
            "Description", reason
        );
    }

    public void logTesterAdded(String executor, String target) {
        String url = plugin.getConfig().getString("webhook-features", "");
        sendEmbed(url, BLUE,
            "➕ Beta Tester Added",
            "**" + target + "** was added by **" + executor + "**",
            null, null);
    }

    public void logTesterRemoved(String executor, String target) {
        String url = plugin.getConfig().getString("webhook-features", "");
        sendEmbed(url, RED,
            "➖ Beta Tester Removed",
            "**" + target + "** was removed by **" + executor + "**",
            null, null);
    }

    private void sendEmbed(String webhookUrl, int color, String title,
                           String description, String fieldName, String fieldValue) {
        if (webhookUrl == null || webhookUrl.isBlank() || webhookUrl.contains("YOUR_")) return;

        String fields = fieldName != null
            ? ",\"fields\":[{\"name\":\"" + esc(fieldName) + "\",\"value\":\"" + esc(fieldValue) + "\",\"inline\":false}]"
            : "";

        String json = "{\"embeds\":[{"
            + "\"title\":\""       + esc(title)       + "\","
            + "\"description\":\"" + esc(description) + "\","
            + "\"color\":"         + color            + ","
            + "\"timestamp\":\""   + Instant.now()    + "\","
            + "\"footer\":{\"text\":\"BetaSystem \u2022 RuneMC\"}"
            + fields
            + "}]}";

        final String fUrl  = webhookUrl;
        final String fJson = json;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection c = (HttpURLConnection) new URL(fUrl).openConnection();
                c.setRequestMethod("POST");
                c.setRequestProperty("Content-Type", "application/json");
                c.setRequestProperty("User-Agent", "BetaSystem");
                c.setDoOutput(true);
                c.setConnectTimeout(5000);
                c.setReadTimeout(5000);
                try (OutputStream os = c.getOutputStream()) {
                    os.write(fJson.getBytes(StandardCharsets.UTF_8));
                }
                c.getResponseCode();
                c.disconnect();
            } catch (Exception e) {
                plugin.getLogger().warning("[Discord] " + e.getMessage());
            }
        });
    }

    private String esc(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
