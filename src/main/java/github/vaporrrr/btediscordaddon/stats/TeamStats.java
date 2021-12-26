package github.vaporrrr.btediscordaddon.stats;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.TimerTask;

public class TeamStats extends TimerTask {
    private final BTEDiscordAddon bteDiscordAddon;
    private final int interval;
    private static final JDA jda = DiscordUtil.getJda();
    private EmbedBuilder embed = new EmbedBuilder();

    public TeamStats(BTEDiscordAddon bteDiscordAddon, int interval) {
        this.bteDiscordAddon = bteDiscordAddon;
        this.interval = interval;
    }

    @Override
    public void run() {
        embed = new EmbedBuilder();
        embed.setTitle("Team Statistics");
        Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
        for (String value : bteDiscordAddon.getConfig().getStringList("Stats.Team.Description")) {
            add(format(value));
        }
        List<String> roles = bteDiscordAddon.getConfig().getStringList("Stats.Team.RoleIDS");
        for (String roleID : roles) {
            Role role = mainGuild.getRoleById(roleID);
            if (role == null) {
                bteDiscordAddon.getLogger().warning("Could not find role " + roleID);
            } else {
                add("**" + role.getName() + " Role Size**: `" + mainGuild.getMembersWithRoles(role).size() + "`");
            }
        }
        embed.setFooter("Updated every " + interval + " seconds");
        TextChannel channel = jda.getTextChannelById(bteDiscordAddon.getConfig().getString("Stats.Team.ChannelID"));
        if (channel != null) {
            channel.editMessageById(bteDiscordAddon.getConfig().getString("Stats.Team.MessageID"), embed.build()).queue();
        } else {
            bteDiscordAddon.getLogger().warning("TextChannel from Stats.Team.ChannelID could not be found");
        }
    }

    private void add(String value) {
        embed.appendDescription("\n" + value);
    }

    private String format(String value) {
        Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
        value = value.replace("$unix$", Long.toString(System.currentTimeMillis() / 1000L));
        value = value.replace("$guild_age_unix$", Long.toString(mainGuild.getTimeCreated().toEpochSecond()));
        value = value.replace("$guild_member_count$", Integer.toString(mainGuild.getMemberCount()));
        value = value.replace("$guild_member_max$", Integer.toString(mainGuild.getMaxMembers()));
        value = value.replace("$guild_category_count$", Integer.toString(mainGuild.getCategories().size()));
        value = value.replace("$guild_channel_voice_count$", Integer.toString(mainGuild.getVoiceChannels().size()));
        value = value.replace("$guild_channel_text_count$", Integer.toString(mainGuild.getTextChannels().size()));
        value = value.replace("$guild_channel_store_count$", Integer.toString(mainGuild.getStoreChannels().size()));
        value = value.replace("$guild_channel_count$", Integer.toString(mainGuild.getChannels().size()));
        value = value.replace("$guild_role_count$", Integer.toString(mainGuild.getRoles().size()));
        value = value.replace("$guild_emote_count$", Integer.toString(mainGuild.getEmotes().size()));
        value = value.replace("$guild_emote_max$", Integer.toString(mainGuild.getMaxEmotes()));
        value = value.replace("$guild_boost_count$", Integer.toString(mainGuild.getBoostCount()));
        value = value.replace("$guild_booster_count$", Integer.toString(mainGuild.getBoosters().size()));
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            value = PlaceholderAPI.setPlaceholders(null, value);
        }
        return value;
    }
}
