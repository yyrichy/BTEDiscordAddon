package github.vaporrrr.btediscordaddon.stats;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class TeamStats extends TimerTask {
    private static final JDA jda = DiscordUtil.getJda();
    private final BTEDiscordAddon bteDiscordAddon;
    private EmbedBuilder embed = new EmbedBuilder();

    public TeamStats(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
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
        embed.setFooter("Updated every " + bteDiscordAddon.getConfig().getInt("Stats.Team.IntervalInSeconds") + " seconds");
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
        JsonObject totalLocations = getRequest("map/data/locations", null);
        if (totalLocations != null) {
            value = value.replace("$bte_project_location_count$", Integer.toString(totalLocations.getAsJsonArray("locations").size()));
        }
        String key = bteDiscordAddon.getConfig().getString("Stats.Team.BTEWebsiteAPIKey");
        if (key != null && !key.isEmpty()) {
            JsonObject locations = getRequest("api/v1/locations", key);
            if (locations != null) {
                value = value.replace("$bte_team_location_count$", Integer.toString(locations.getAsJsonArray("locations").size()));
            }
            JsonObject pending = getRequest("api/v1/applications/pending", key);
            if (pending != null) {
                value = value.replace("$bte_team_applications_pending_count$", Integer.toString(pending.getAsJsonArray("applications").size()));
            }
            JsonObject members = getRequest("api/v1/members", key);
            if (members != null) {
                value = value.replace("$bte_team_member_count$", Integer.toString(members.getAsJsonArray("members").size()));
                int leaders = 0;
                int coLeaders = 0;
                int reviewers = 0;
                int builders = 0;
                ArrayList<String> leaderList = new ArrayList<>();
                ArrayList<String> coLeaderList = new ArrayList<>();
                ArrayList<String> reviewerList = new ArrayList<>();
                ArrayList<String> builderList = new ArrayList<>();
                for (JsonElement member : members.getAsJsonArray("members")) {
                    switch (member.getAsJsonObject().get("role").getAsString()) {
                        case "leader":
                            leaders++;
                            leaderList.add(member.getAsJsonObject().get("discordTag").getAsString());
                            break;
                        case "co-leader":
                            coLeaders++;
                            coLeaderList.add(member.getAsJsonObject().get("discordTag").getAsString());
                            break;
                        case "reviewer":
                            reviewers++;
                            reviewerList.add(member.getAsJsonObject().get("discordTag").getAsString());
                            break;
                        case "builder":
                            builders++;
                            builderList.add(member.getAsJsonObject().get("discordTag").getAsString());
                            break;
                    }
                }
                value = value.replace("$bte_team_leader_count$", Integer.toString(leaders));
                value = value.replace("$bte_team_co-leader_count$", Integer.toString(coLeaders));
                value = value.replace("$bte_team_reviewer_count$", Integer.toString(reviewers));
                value = value.replace("$bte_team_builder_count$", Integer.toString(builders));
                value = value.replace("$bte_team_leader_list$", String.join("\n", leaderList));
                value = value.replace("$bte_team_co-leader_list$", String.join("\n", coLeaderList));
                value = value.replace("$bte_team_reviewer_list$", String.join("\n", reviewerList));
                value = value.replace("$bte_team_builder_list$", String.join("\n", builderList));
            }
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            value = PlaceholderAPI.setPlaceholders(null, value);
        }
        return value;
    }

    private JsonObject getRequest(String endpoint, String key) {
        try {
            String URL = "https://buildtheearth.net/" + endpoint;
            URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            if (key != null) {
                con.setRequestProperty("Authorization", "Bearer " + key);
            }
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
            con.setRequestMethod("GET");
            JsonObject jsonObject = null;
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            int code = con.getResponseCode();
            if (code >= 200 && code <= 299) {
                jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
            } else {
                bteDiscordAddon.getLogger().warning("Request to https://buildtheearth.net/" + endpoint + " not successful. Response code: " + code);
            }
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
            bteDiscordAddon.getLogger().warning("Unexpected exception making GET request to https://buildtheearth.net/" + endpoint);
            return null;
        }
    }
}
