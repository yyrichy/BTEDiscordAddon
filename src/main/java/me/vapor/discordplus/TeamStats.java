package me.vapor.discordplus;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.TimerTask;

public class TeamStats extends TimerTask {
    private Plugin plugin;
    private int interval;
    private static JDA jda = DiscordUtil.getJda();
    private EmbedBuilder embed = new EmbedBuilder();

    public TeamStats(Plugin plugin, int interval) {
        this.plugin = plugin;
        this.interval = interval;
    }

    @Override
    public void run() {
        final long unixTime = System.currentTimeMillis() / 1000L;
        Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
        List<String> roles = plugin.getConfig().getStringList("TeamStatsRoleIDS");

        embed = new EmbedBuilder();
        embed.setTitle("Team Statistics");
        add("Last Updated", "<t:" + unixTime + ":R>");
        add("Guild Members", "`" + mainGuild.getMembers().size() + "`");
        for(String roleID : roles){
            Role role = mainGuild.getRoleById(roleID);
            if (role == null) {
                plugin.getLogger().warning("Could not find role " + roleID);
            } else {
                embed.addField(role.getName() + " Role Size", "`" + mainGuild.getMembersWithRoles(role).size() + "`", false);
            }
        }
        embed.setFooter("Updated every " + interval + " seconds");
        jda.getTextChannelById(plugin.getConfig().getString("ChannelID")).editMessageById(plugin.getConfig().getString("TeamStatsMessageID"), embed.build()).queue();
    }

    private void add(String name, String value) {
        embed.appendDescription("\n**" + name + "**: " + value);
    }
}
