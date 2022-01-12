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

package com.github.vaporrrr.btediscordaddon.stats;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import de.leonhard.storage.Config;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MinecraftStats extends TimerTask {
    private EmbedBuilder embed = new EmbedBuilder();

    @Override
    public void run() {
        Config config = BTEDiscordAddon.config();
        embed = new EmbedBuilder();
        embed.setTitle("Minecraft Server Statistics");
        for (String value : config.getStringList("Stats.Minecraft.Description")) {
            append(format(value));
        }
        List<String> groupNames = config.getStringList("Stats.Minecraft.GroupNames");
        if (!groupNames.isEmpty()) {
            if (BTEDiscordAddon.getPlugin().getLuckPerms() != null) {
                for (String name : groupNames) {
                    int groupSize = BTEDiscordAddon.getPlugin().getLuckPerms().getGroupSize(name);
                    if (groupSize == -1) {
                        BTEDiscordAddon.warn("Could not get group size of group " + name);
                    } else {
                        append("**" + name + " Group Size**: `" + groupSize + "`");
                    }
                }
            } else {
                BTEDiscordAddon.warn("Stats.Minecraft.GroupNames is not empty, but dependency LuckPerms could not be found. Install LuckPerms.");
            }
        }
        embed.setFooter("Updated every " + config.getInt("Stats.Minecraft.IntervalInSeconds") + " seconds");
        TextChannel channel = DiscordUtil.getJda().getTextChannelById(config.getString("Stats.Minecraft.ChannelID"));
        if (channel != null) {
            channel.retrieveMessageById(config.getString("Stats.Minecraft.MessageID")).queue((message) -> message.editMessage(embed.build()).queue(), (failure) -> BTEDiscordAddon.severe("Could not edit message Stats.Minecraft.MessageID in #" + channel.getName()));
        } else {
            BTEDiscordAddon.warn("TextChannel from Stats.Minecraft.ChannelID could not be found");
        }
    }

    private void append(String value) {
        embed.appendDescription("\n" + value);
    }

    private static String format(String value) {
        Runtime r = Runtime.getRuntime();
        long usedMemory = (r.totalMemory() - r.freeMemory()) / 1048576;
        long maxMemory = r.maxMemory() / 1048576;
        float memory = ((float) usedMemory / (float) maxMemory) * 100;
        long milliseconds = ManagementFactory.getRuntimeMXBean().getUptime();
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        value = value.replace("$current_unix$", Long.toString(System.currentTimeMillis() / 1000L));
        value = value.replace("$unique_players_joined$", Integer.toString(Bukkit.getOfflinePlayers().length));
        value = value.replace("$linked_players$", Integer.toString(DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccountCount()));
        value = value.replace("$memory$", String.format("`%.2f", memory) + "`% | `" + usedMemory + "`/`" + maxMemory + "` MB");
        value = value.replace("$uptime$", String.format("`%d` Days `%02d` Hours `%02d` Minutes", days, hours, minutes));
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            value = PlaceholderAPI.setPlaceholders(null, value);
        }
        return value;
    }
}
