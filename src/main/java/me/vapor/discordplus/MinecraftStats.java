package me.vapor.discordplus;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.TimerTask;

public class MinecraftStats extends TimerTask {
    private Plugin plugin;
    private static JDA jda = DiscordUtil.getJda();
    public MinecraftStats(Plugin plugin){
        this.plugin = plugin;
    }
    @Override
    public void run() {
        TextChannel channel = jda.getTextChannelById("823988393963683850");
        if(channel != null){
            Runtime r = Runtime.getRuntime();
            long usedMemory = (r.totalMemory() - r.freeMemory()) / 1048576;
            long maxMemory = r.maxMemory() / 1048576;
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Minecraft Server Statistics");
            embed.addField("Unique Players Joined", String.valueOf(Bukkit.getOfflinePlayers().length), false);
            embed.addField("Linked Players", String.valueOf(DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccountCount()), false);
            embed.addField("Memory", usedMemory + "/" + maxMemory + " MB", false);
            channel.editMessageById("880212319198658620", embed.build()).queue();
            plugin.getLogger().info("Updated Minecraft Stats Embed.");
        } else {
            plugin.getLogger().warning("channel null");
        }
    }
}
