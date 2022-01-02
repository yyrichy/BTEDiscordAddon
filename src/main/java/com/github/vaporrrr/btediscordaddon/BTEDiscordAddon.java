package com.github.vaporrrr.btediscordaddon;

import com.github.vaporrrr.btediscordaddon.commands.minecraft.Afk;
import com.github.vaporrrr.btediscordaddon.commands.minecraft.Online;
import com.github.vaporrrr.btediscordaddon.commands.minecraft.Reload;
import com.github.vaporrrr.btediscordaddon.commands.minecraft.Update;
import github.scarsz.discordsrv.DiscordSRV;
import com.github.vaporrrr.btediscordaddon.listeners.BukkitListener;
import com.github.vaporrrr.btediscordaddon.listeners.DiscordListener;
import com.github.vaporrrr.btediscordaddon.stats.MinecraftStats;
import com.github.vaporrrr.btediscordaddon.stats.TeamStats;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Timer;

public class BTEDiscordAddon extends JavaPlugin {
    private final DiscordListener discordSRVListener = new DiscordListener(this);
    private final UserManager userManager = new UserManager(this);
    private final ServerStatus serverStatus = new ServerStatus(this);
    private final Timer t = new Timer();
    private MinecraftStats mcStats = new MinecraftStats(this);
    private TeamStats teamStats = new TeamStats(this);
    private LP luckPerms = null;

    @Override
    public void onEnable() {
        getLogger().info("Enabled!");
        getConfig().options().copyDefaults(true);
        saveConfig();
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            this.luckPerms = new LP();
        }
        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
        getCommand("afk").setExecutor(new Afk(this));
        getCommand("online").setExecutor(new Online(this));
        getCommand("bted-reload").setExecutor(new Reload(this));
        getCommand("bted-update").setExecutor(new Update(this));
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

    public LP getLuckPerms() {
        return luckPerms;
    }

    public void restartStats() {
        mcStats.cancel();
        teamStats.cancel();
        startStats();
    }

    public void startStats() {
        if (getConfig().getBoolean("Stats.Minecraft.Enabled")) {
            mcStats = new MinecraftStats(this);
            t.scheduleAtFixedRate(mcStats, 0, getConfig().getInt("Stats.Minecraft.IntervalInSeconds") * 1000L);
        }
        if (getConfig().getBoolean("Stats.Team.Enabled")) {
            teamStats = new TeamStats(this);
            t.scheduleAtFixedRate(teamStats, 0, getConfig().getInt("Stats.Team.IntervalInSeconds") * 1000L);
        }
    }
}

