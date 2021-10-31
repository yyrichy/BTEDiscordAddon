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
        final long usedMemory = (r.totalMemory() - r.freeMemory()) / 1048576;
        final long maxMemory = r.maxMemory() / 1048576;
        final long unixTime = System.currentTimeMillis() / 1000L;
        final long milliseconds = ManagementFactory.getRuntimeMXBean().getUptime();
        final long dys = TimeUnit.MILLISECONDS.toDays(milliseconds);
        final long hrs = TimeUnit.MILLISECONDS.toHours(milliseconds)  - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        final long mins = TimeUnit.MILLISECONDS.toMinutes(milliseconds)  - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        final long secs = TimeUnit.MILLISECONDS.toSeconds(milliseconds)  - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        String uptime = String.format("%d Days %d Hours %d Minutes %d Seconds", dys, hrs, mins, secs);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Minecraft Server Statistics");
        embed.setDescription("Last Updated: <t:" + unixTime + ":R>");
        embed.addField("Unique Players Joined", String.valueOf(Bukkit.getOfflinePlayers().length), false);
        embed.addField("Linked Players", String.valueOf(DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccountCount()), false);
        embed.addField("Memory", usedMemory + "/" + maxMemory + " MB", false);
        embed.addField("Uptime", uptime, false);
        jda.getTextChannelById(plugin.getConfig().getString("ChannelID")).editMessageById(plugin.getConfig().getString("MinecraftStatsMessageID"), embed.build()).queue();
    }
}
