package me.vapor.discordplus;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinecraftStats extends TimerTask {
    private Plugin plugin;
    private static JDA jda = DiscordUtil.getJda();
    private int interval;
    LuckPerms luckPerms;

    public MinecraftStats(Plugin plugin, int interval) {
        this.plugin = plugin;
        this.interval = interval;
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
    }

    @Override
    public void run() {
        Runtime r = Runtime.getRuntime();
        long usedMemory = (r.totalMemory() - r.freeMemory()) / 1048576;
        long maxMemory = r.maxMemory() / 1048576;
        if(luckPerms == null){
           luckPerms = LuckPermsProvider.get();
        }
        long unixTime = System.currentTimeMillis() / 1000L;
        long milliseconds = ManagementFactory.getRuntimeMXBean().getUptime();
        long dys = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hrs = TimeUnit.MILLISECONDS.toHours(milliseconds)  - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        long mins = TimeUnit.MILLISECONDS.toMinutes(milliseconds)  - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        float memory = ((float) usedMemory / (float) maxMemory) * 100;
        List<String> groupNames = plugin.getConfig().getStringList("MinecraftStatsGroupNames");

        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Minecraft Server Statistics");
        embed.setDescription("Last Updated: <t:" + unixTime + ":R>");
        embed.addField("Unique Players Joined", "`" + Bukkit.getOfflinePlayers().length + "`", false);
        embed.addField("Linked Players", "`" + DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccountCount() + "`", false);
        embed.addField("Memory", "`" + String.format("%.2f", memory) + "`% | `" + usedMemory + "`/`" + maxMemory + "` MB", false);
        embed.addField("Uptime", String.format("`%d` Days `%02d` Hours `%02d` Minutes", dys, hrs, mins), false);
        for(String name : groupNames){
            embed.addField(name + " Group Size", "`" + getNumUsersInGroup(name) + "`", false);
        }
        embed.setFooter("Updated every " + interval + " seconds");
        jda.getTextChannelById(plugin.getConfig().getString("ChannelID")).editMessageById(plugin.getConfig().getString("MinecraftStatsMessageID"), embed.build()).queue();
    }

    private int getNumUsersInGroup(String groupName) {
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if(group == null) return -1;
        NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder(group).build());
        return luckPerms.getUserManager().searchAll(matcher).join().size();
    }
}
