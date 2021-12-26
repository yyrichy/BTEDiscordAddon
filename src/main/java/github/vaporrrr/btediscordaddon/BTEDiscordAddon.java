package github.vaporrrr.btediscordaddon;

import github.scarsz.discordsrv.DiscordSRV;
import github.vaporrrr.btediscordaddon.commands.minecraft.Afk;
import github.vaporrrr.btediscordaddon.commands.minecraft.Reload;
import github.vaporrrr.btediscordaddon.commands.minecraft.Update;
import github.vaporrrr.btediscordaddon.listeners.BukkitListener;
import github.vaporrrr.btediscordaddon.listeners.DiscordListener;
import github.vaporrrr.btediscordaddon.stats.MinecraftStats;
import github.vaporrrr.btediscordaddon.stats.TeamStats;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;

public class BTEDiscordAddon extends JavaPlugin {
    private final DiscordListener discordSRVListener = new DiscordListener(this);
    private final UserManager userManager = new UserManager(this);
    private final ServerStatus serverStatus = new ServerStatus(this);
    private final MinecraftStats mcStats = new MinecraftStats(this);
    private final TeamStats teamStats = new TeamStats(this);

    @Override
    public void onEnable() {
        getLogger().info("Enabled!");
        getConfig().options().copyDefaults(true);
        saveConfig();
        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
        getCommand("bted-update").setExecutor(new Update(this));
        getCommand("bted-reload").setExecutor(new Reload(this));
        getCommand("afk").setExecutor(new Afk(this));
        DiscordSRV.api.subscribe(discordSRVListener);
    }

    public void onDisable() {
        serverStatus.shutdown();
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }

    public void restartStats() {
        Timer t = new Timer();
        mcStats.cancel();
        teamStats.cancel();
        t.scheduleAtFixedRate(mcStats, 0, getConfig().getInt("Stats.Minecraft.IntervalInSeconds") * 1000L);
        t.scheduleAtFixedRate(teamStats, 0, getConfig().getInt("Stats.Team.IntervalInSeconds") * 1000L);
    }
}

