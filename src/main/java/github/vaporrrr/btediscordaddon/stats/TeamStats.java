package github.vaporrrr.btediscordaddon.stats;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.TimerTask;

public class TeamStats extends TimerTask {
    private final Plugin plugin;
    private final int interval;
    private static final JDA jda = DiscordUtil.getJda();
    private EmbedBuilder embed = new EmbedBuilder();

    public TeamStats(Plugin plugin, int interval) {
        this.plugin = plugin;
        this.interval = interval;
    }

    @Override
    public void run() {
        final long unixTime = System.currentTimeMillis() / 1000L;
        Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
        List<String> roles = plugin.getConfig().getStringList("Stats.Team.RoleIDS");

        embed = new EmbedBuilder();
        embed.setTitle("Team Statistics");
        add("Last Updated", "<t:" + unixTime + ":R>");
        add("Guild Members", "`" + mainGuild.getMembers().size() + "`");
        for (String roleID : roles) {
            Role role = mainGuild.getRoleById(roleID);
            if (role == null) {
                plugin.getLogger().warning("Could not find role " + roleID);
            } else {
                embed.addField(role.getName() + " Role Size", "`" + mainGuild.getMembersWithRoles(role).size() + "`", false);
            }
        }
        embed.setFooter("Updated every " + interval + " seconds");
        TextChannel channel = jda.getTextChannelById(plugin.getConfig().getString("Stats.Team.ChannelID"));
        if (channel != null) {
            channel.editMessageById(plugin.getConfig().getString("Stats.Team.MessageID"), embed.build()).queue();
        } else {
            plugin.getLogger().warning("TextChannel from Stats.Team.ChannelID could not be found");
        }
    }

    private void add(String name, String value) {
        embed.appendDescription("\n**" + name + "**: " + value);
    }
}
