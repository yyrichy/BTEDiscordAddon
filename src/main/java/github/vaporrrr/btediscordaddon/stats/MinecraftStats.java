package github.vaporrrr.btediscordaddon.stats;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import github.vaporrrr.btediscordaddon.LP;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MinecraftStats extends TimerTask {
    private final Plugin bteDiscordAddon;
    private static final JDA jda = DiscordUtil.getJda();
    private final int interval;
    private EmbedBuilder embed = new EmbedBuilder();
    private LP luckPerms = null;

    public MinecraftStats(BTEDiscordAddon bteDiscordAddon, int interval) {
        this.bteDiscordAddon = bteDiscordAddon;
        this.interval = interval;
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            this.luckPerms = new LP();
        }
    }

    @Override
    public void run() {
        Runtime r = Runtime.getRuntime();
        long usedMemory = (r.totalMemory() - r.freeMemory()) / 1048576;
        long maxMemory = r.maxMemory() / 1048576;
        long unixTime = System.currentTimeMillis() / 1000L;
        long milliseconds = ManagementFactory.getRuntimeMXBean().getUptime();
        long dys = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hrs = TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        long minis = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        float memory = ((float) usedMemory / (float) maxMemory) * 100;
        List<String> groupNames = bteDiscordAddon.getConfig().getStringList("Stats.Minecraft.GroupNames");

        embed = new EmbedBuilder();
        embed.setTitle("Minecraft Server Statistics");
        add("Last Updated", "<t:" + unixTime + ":R>");
        add("Unique Players Joined", "`" + Bukkit.getOfflinePlayers().length + "`");
        add("Linked Players", "`" + DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccountCount() + "`");
        add("Memory", "`" + String.format("%.2f", memory) + "`% | `" + usedMemory + "`/`" + maxMemory + "` MB");
        add("Uptime", String.format("`%d` Days `%02d` Hours `%02d` Minutes", dys, hrs, minis));
        if (!groupNames.isEmpty()) {
            if (luckPerms != null) {
                for (String name : groupNames) {
                    int groupSize = luckPerms.getGroupSize(name);
                    if (groupSize == -1) {
                        bteDiscordAddon.getLogger().warning("Could not get group size of group " + name);
                    } else {
                        embed.addField(name + " Group Size", "`" + groupSize + "`", false);
                    }
                }
            } else {
                bteDiscordAddon.getLogger().warning("Stats.Minecraft.GroupNames is not empty, but dependency LuckPerms could not be found. Install LuckPerms.");
            }
        }
        embed.setFooter("Updated every " + interval + " seconds");
        TextChannel channel = jda.getTextChannelById(bteDiscordAddon.getConfig().getString("Stats.Minecraft.ChannelID"));
        if (channel != null) {
            channel.editMessageById(bteDiscordAddon.getConfig().getString("Stats.Minecraft.MessageID"), embed.build()).queue();
        } else {
            bteDiscordAddon.getLogger().warning("TextChannel from Stats.Minecraft.ChannelID could not be found");
        }
    }

    private void add(String name, String value) {
        embed.appendDescription("\n**" + name + "**: " + value);
    }
}
