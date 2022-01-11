/*
 * BTEDiscordAddon
 * Copyright 2022 (C) vaporrrr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.vaporrrr.btediscordaddon;

import com.github.vaporrrr.btediscordaddon.commands.minecraft.Afk;
import com.github.vaporrrr.btediscordaddon.commands.minecraft.Online;
import com.github.vaporrrr.btediscordaddon.commands.minecraft.Reload;
import com.github.vaporrrr.btediscordaddon.commands.minecraft.Update;
import com.github.vaporrrr.btediscordaddon.luckperms.LP;
import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.discordsrv.DiscordSRV;
import com.github.vaporrrr.btediscordaddon.listeners.BukkitListener;
import com.github.vaporrrr.btediscordaddon.listeners.DiscordListener;
import com.github.vaporrrr.btediscordaddon.stats.MinecraftStats;
import com.github.vaporrrr.btediscordaddon.stats.TeamStats;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

public class BTEDiscordAddon extends JavaPlugin {
    private final DynamicConfig config = new DynamicConfig();
    private final DiscordListener discordSRVListener = new DiscordListener(this);
    private final UserManager userManager = new UserManager(this);
    private final ServerStatus serverStatus = new ServerStatus(this);
    private final Timer t = new Timer();
    private MinecraftStats mcStats = new MinecraftStats(this);
    private TeamStats teamStats = new TeamStats(this);
    private LP luckPerms = null;
    private final File configFile = new File(getDataFolder(), "config.yml");

    @Override
    public void onEnable() {
        getLogger().info("Enabled!");

        config.addSource(BTEDiscordAddon.class, "config", configFile);
        try {
            config.saveAllDefaults();
        } catch (IOException e) {
            throw new RuntimeException("Unable to save all default config files.");
        }
        try {
            config.loadAll();
        } catch (Exception e) {
            throw new RuntimeException("Unable to load config");
        }

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

    public DynamicConfig config() {
        return config;
    }

    public void reloadConfig() {
        try {
            config().loadAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config", e);
        }
    }

    public void info(String message) {
        getLogger().info(message);
    }

    public void warn(String message) {
        getLogger().warning(message);
    }

    public void severe(String message) {
        getLogger().severe(message);
    }

    public void restartStats() {
        mcStats.cancel();
        teamStats.cancel();
        startStats();
    }

    public void startStats() {
        if (getConfig().getBoolean("Stats.Minecraft.Enabled")) {
            mcStats = new MinecraftStats(this);
            t.scheduleAtFixedRate(mcStats, 0, config.getInt("Stats.Minecraft.IntervalInSeconds") * 1000L);
        }
        if (getConfig().getBoolean("Stats.Team.Enabled")) {
            teamStats = new TeamStats(this);
            t.scheduleAtFixedRate(teamStats, 0, config.getInt("Stats.Team.IntervalInSeconds") * 1000L);
        }
    }
}

