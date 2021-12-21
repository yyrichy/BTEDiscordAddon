package github.vaporrrr.btediscordaddon.stats;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
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
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MinecraftStats extends TimerTask {
    private final Plugin plugin;
    private static final JDA jda = DiscordUtil.getJda();
    private final int interval;
    private EmbedBuilder embed = new EmbedBuilder();
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
        long minis = TimeUnit.MILLISECONDS.toMinutes(milliseconds)  - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        float memory = ((float) usedMemory / (float) maxMemory) * 100;
        List<String> groupNames = plugin.getConfig().getStringList("Stats.Minecraft.GroupNames");

        embed = new EmbedBuilder();
        embed.setTitle("Minecraft Server Statistics");
        add("Last Updated", "<t:" + unixTime + ":R>");
        add("Unique Players Joined", "`" + Bukkit.getOfflinePlayers().length + "`");
        add("Linked Players", "`" + DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccountCount() + "`");
        add("Memory", "`" + String.format("%.2f", memory) + "`% | `" + usedMemory + "`/`" + maxMemory + "` MB");
        add("Uptime", String.format("`%d` Days `%02d` Hours `%02d` Minutes", dys, hrs, minis));
        for(String name : groupNames){
            int groupSize = getGroupSize(name);
            if (groupSize == -1) {
                plugin.getLogger().warning("Could not get group size of group " + name);
            } else {
                embed.addField(name + " Group Size", "`" + groupSize + "`", false);
            }
        }
        embed.setFooter("Updated every " + interval + " seconds");
        TextChannel channel = jda.getTextChannelById(plugin.getConfig().getString("Stats.Minecraft.ChannelID"));
        if (channel != null) {
            channel.editMessageById(plugin.getConfig().getString("Stats.Minecraft.MessageID"), embed.build()).queue();
        } else {
            plugin.getLogger().warning("TextChannel from Stats.Minecraft.ChannelID could not be found");
        }
    }

    private int getGroupSize(String groupName) {
        Group group = luckPerms.getGroupManager().getGroup(groupName);
        if(group == null) return -1;
        NodeMatcher<InheritanceNode> matcher = NodeMatcher.key(InheritanceNode.builder(group).build());
        return luckPerms.getUserManager().searchAll(matcher).join().size();
    }

    private void add(String name, String value) {
        embed.appendDescription("\n**" + name + "**: " + value);
    }
}
