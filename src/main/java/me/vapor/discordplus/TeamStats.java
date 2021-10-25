package me.vapor.discordplus;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.Plugin;

import java.util.TimerTask;

public class TeamStats extends TimerTask {
    private Plugin plugin;
    private static JDA jda = DiscordUtil.getJda();
    public TeamStats(Plugin plugin){
        this.plugin = plugin;
    }
    @Override
    public void run(){
        TextChannel channel = jda.getTextChannelById("823988393963683850");
        if(channel != null){
            EmbedBuilder embed = new EmbedBuilder();
            Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
            Role role = mainGuild.getRoleById(plugin.getConfig().getString("TeamStatsRoleID"));
            if(role == null){
                plugin.getLogger().warning("TeamStatsRoleID not set correctly in config");
                return;
            }
            embed.setTitle("Team Statistics");
            embed.addField("Guild Members", String.valueOf(mainGuild.getMembers().size()), false);
            embed.addField(role.getName() + " Role Size", String.valueOf(mainGuild.getMembersWithRoles(role).size()), false);
            channel.editMessageById("881330867706941502", embed.build()).queue();
            plugin.getLogger().info("Updated Team Stats Embed.");
        } else {
            plugin.getLogger().warning("channel null");
        }
    }
}
