package me.vapor.discordplus;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.management.ManagementFactory;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MinecraftStats extends TimerTask {
    private Plugin plugin;
    private static JDA jda = DiscordUtil.getJda();

    public MinecraftStats(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        Runtime r = Runtime.getRuntime();
        long usedMemory = (r.totalMemory() - r.freeMemory()) / 1048576;
        long maxMemory = r.maxMemory() / 1048576;
        long unixTime = System.currentTimeMillis() / 1000L;
        long milliseconds = ManagementFactory.getRuntimeMXBean().getUptime();
        long dys = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hrs = TimeUnit.MILLISECONDS.toHours(milliseconds)  - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        long mins = TimeUnit.MILLISECONDS.toMinutes(milliseconds)  - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        float memory = ((float) usedMemory / (float) maxMemory) * 100;

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Minecraft Server Statistics");
        embed.setDescription("Last Updated: <t:" + unixTime + ":R>");
        embed.addField("Unique Players Joined", "`" + Bukkit.getOfflinePlayers().length + "`", false);
        embed.addField("Linked Players", "`" + DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccountCount() + "`", false);
        embed.addField("Memory", "`" + String.format("%.2f", memory) + "`% | `" + usedMemory + "`/`" + maxMemory + "` MB", false);
        embed.addField("Uptime", String.format("`%d` Days `%02d` Hours `%02d` Minutes", dys, hrs, mins), false);
        jda.getTextChannelById(plugin.getConfig().getString("ChannelID")).editMessageById(plugin.getConfig().getString("MinecraftStatsMessageID"), embed.build()).queue();
    }
}
