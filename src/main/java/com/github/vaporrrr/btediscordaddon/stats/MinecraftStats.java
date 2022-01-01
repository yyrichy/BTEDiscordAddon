package com.github.vaporrrr.btediscordaddon.stats;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.LP;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MinecraftStats extends TimerTask {
    private final Plugin bteDiscordAddon;
    private EmbedBuilder embed = new EmbedBuilder();
    private LP luckPerms = null;

    public MinecraftStats(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            this.luckPerms = new LP();
        }
    }

    @Override
    public void run() {
        embed = new EmbedBuilder();
        embed.setTitle("Minecraft Server Statistics");
        for (String value : bteDiscordAddon.getConfig().getStringList("Stats.Minecraft.Description")) {
            add(format(value));
        }
        List<String> groupNames = bteDiscordAddon.getConfig().getStringList("Stats.Minecraft.GroupNames");
        if (!groupNames.isEmpty()) {
            if (luckPerms != null) {
                for (String name : groupNames) {
                    int groupSize = luckPerms.getGroupSize(name);
                    if (groupSize == -1) {
                        bteDiscordAddon.getLogger().warning("Could not get group size of group " + name);
                    } else {
                        add("**" + name + " Group Size**: `" + groupSize + "`");
                    }
                }
            } else {
                bteDiscordAddon.getLogger().warning("Stats.Minecraft.GroupNames is not empty, but dependency LuckPerms could not be found. Install LuckPerms.");
            }
        }
        embed.setFooter("Updated every " + bteDiscordAddon.getConfig().getInt("Stats.Minecraft.IntervalInSeconds") + " seconds");
        TextChannel channel = DiscordUtil.getJda().getTextChannelById(bteDiscordAddon.getConfig().getString("Stats.Minecraft.ChannelID"));
        if (channel != null) {
            channel.editMessageById(bteDiscordAddon.getConfig().getString("Stats.Minecraft.MessageID"), embed.build()).queue();
        } else {
            bteDiscordAddon.getLogger().warning("TextChannel from Stats.Minecraft.ChannelID could not be found");
        }
    }

    private void add(String value) {
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
