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
import com.github.vaporrrr.btediscordaddon.listeners.BukkitListener;
import com.github.vaporrrr.btediscordaddon.listeners.DiscordListener;
import com.github.vaporrrr.btediscordaddon.luckperms.LP;
import com.github.vaporrrr.btediscordaddon.stats.MinecraftStats;
import com.github.vaporrrr.btediscordaddon.stats.TeamStats;
import de.leonhard.storage.Config;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.ConfigSettings;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.util.Timer;

public class BTEDiscordAddon extends JavaPlugin {
    private final Config config;
    private final DiscordListener discordSRVListener = new DiscordListener();
    private final UserManager userManager = new UserManager();
    private final ServerStatus serverStatus = new ServerStatus();
    private final Timer t = new Timer();
    private MinecraftStats mcStats = new MinecraftStats();
    private TeamStats teamStats = new TeamStats();
    private LP luckPerms = null;

    public BTEDiscordAddon() {
        super();
        InputStream is = getClassLoader().getResourceAsStream("config.yml");
        config = LightningBuilder
                .fromFile(new File(getDataFolder(), "config.yml"))
                .addInputStream(is)
                .setDataType(DataType.SORTED)
                .setConfigSettings(ConfigSettings.PRESERVE_COMMENTS)
                .setReloadSettings(ReloadSettings.MANUALLY)
                .createConfig()
                .addDefaultsFromInputStream(is);
    }

    @Override
    public void onEnable() {
        getLogger().info("Enabled!");
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            this.luckPerms = new LP();
        }
        getServer().getPluginManager().registerEvents(new BukkitListener(), this);
        getCommand("afk").setExecutor(new Afk());
        getCommand("online").setExecutor(new Online());
        getCommand("bted-reload").setExecutor(new Reload());
        getCommand("bted-update").setExecutor(new Update());
        DiscordSRV.api.subscribe(discordSRVListener);
    }

    public void onDisable() {
        getServerStatus().shutdown();
    }

    public static BTEDiscordAddon getPlugin() {
        return getPlugin(BTEDiscordAddon.class);
    }

    public static Config config() {
        return getPlugin().config;
    }

    public void reloadConfig() {
        config().forceReload();
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

    public static void info(String message) {
        getPlugin().getLogger().info(message);
    }

    public static void warn(String message) {
        getPlugin().getLogger().warning(message);
    }

    public static void severe(String message) {
        getPlugin().getLogger().severe(message);
    }

    public void restartStats() {
        mcStats.cancel();
        teamStats.cancel();
        startStats();
    }

    public void startStats() {
        if (config().getBoolean("Stats.Minecraft.Enabled")) {
            mcStats = new MinecraftStats();
            t.scheduleAtFixedRate(getPlugin().mcStats, 0, config().getInt("Stats.Minecraft.IntervalInSeconds") * 1000L);
        }
        if (config().getBoolean("Stats.Team.Enabled")) {
            teamStats = new TeamStats();
            t.scheduleAtFixedRate(teamStats, 0, config().getInt("Stats.Team.IntervalInSeconds") * 1000L);
        }
    }
}

