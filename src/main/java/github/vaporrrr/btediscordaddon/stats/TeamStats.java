package github.vaporrrr.btediscordaddon.stats;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class TeamStats extends TimerTask {
    private final BTEDiscordAddon bteDiscordAddon;
    private EmbedBuilder embed = new EmbedBuilder();
    private int projectLocations = -1;
    private int teamLocations = -1;
    private int pendingApplications = -1;
    private int websiteMembers = -1;
    private int leaders = -1;
    private int coLeaders = -1;
    private int reviewers = -1;
    private int builders = -1;
    private final ArrayList<String> leaderList = new ArrayList<>();
    private final ArrayList<String> coLeaderList = new ArrayList<>();
    private final ArrayList<String> reviewerList = new ArrayList<>();
    private final ArrayList<String> builderList = new ArrayList<>();

    public TeamStats(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
    }

    @Override
    public void run() {
        embed = new EmbedBuilder();
        embed.setTitle("Team Statistics");
        Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
        reset();
        JSONObject totalLocations = getRequest("map/data/locations", null);
        if (totalLocations != null) {
            projectLocations = totalLocations.getJSONArray("locations").length();
        }
        String key = bteDiscordAddon.getConfig().getString("Stats.Team.BTEWebsiteAPIKey");
        if (key != null && !key.isEmpty()) {
            JSONObject locations = getRequest("api/v1/locations", key);
            if (locations != null) {
                teamLocations = locations.getJSONArray("locations").length();
            }
            JSONObject pending = getRequest("api/v1/applications/pending", key);
            if (pending != null) {
                pendingApplications = pending.getJSONArray("applications").length();
            }
            JSONObject members = getRequest("api/v1/members", key);
            if (members != null) {
                websiteMembers = 0;
                leaders = 0;
                coLeaders = 0;
                reviewers = 0;
                builders = 0;
                websiteMembers = members.getJSONArray("members").length();
                for (int i = 0; i < members.getJSONArray("members").length(); i++) {
                    JSONObject member = members.getJSONArray("members").getJSONObject(i);
                    switch (member.getString("role")) {
                        case "leader":
                            leaders++;
                            leaderList.add(member.getString("discordTag"));
                            break;
                        case "co-leader":
                            coLeaders++;
                            coLeaderList.add(member.getString("discordTag"));
                            break;
                        case "reviewer":
                            reviewers++;
                            reviewerList.add(member.getString("discordTag"));
                            break;
                        case "builder":
                            builders++;
                            builderList.add(member.getString("discordTag"));
                            break;
                    }
                }
            }
        }
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
        TextChannel channel = DiscordUtil.getJda().getTextChannelById(bteDiscordAddon.getConfig().getString("Stats.Team.ChannelID"));
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
        value = value.replace("$guild_members$", Integer.toString(mainGuild.getMemberCount()));
        value = value.replace("$guild_member_max$", Integer.toString(mainGuild.getMaxMembers()));
        value = value.replace("$guild_categories$", Integer.toString(mainGuild.getCategories().size()));
        value = value.replace("$guild_channel_voice$", Integer.toString(mainGuild.getVoiceChannels().size()));
        value = value.replace("$guild_channel_text$", Integer.toString(mainGuild.getTextChannels().size()));
        value = value.replace("$guild_channel_store$", Integer.toString(mainGuild.getStoreChannels().size()));
        value = value.replace("$guild_channels$", Integer.toString(mainGuild.getChannels().size()));
        value = value.replace("$guild_roles$", Integer.toString(mainGuild.getRoles().size()));
        value = value.replace("$guild_emotes$", Integer.toString(mainGuild.getEmotes().size()));
        value = value.replace("$guild_emote_max$", Integer.toString(mainGuild.getMaxEmotes()));
        value = value.replace("$guild_boosts$", Integer.toString(mainGuild.getBoostCount()));
        value = value.replace("$guild_boosters$", Integer.toString(mainGuild.getBoosters().size()));
        if (projectLocations != -1) {
            value = value.replace("$bte_project_locations$", Integer.toString(projectLocations));
        }
        if (teamLocations != -1) {
            value = value.replace("$bte_team_locations$", Integer.toString(teamLocations));
        }
        if (pendingApplications != -1) {
            value = value.replace("$bte_team_applications_pending$", Integer.toString(pendingApplications));
        }
        if (websiteMembers != -1) {
            value = value.replace("$bte_team_members$", Integer.toString(websiteMembers));
            value = value.replace("$bte_team_leaders$", Integer.toString(leaders));
            value = value.replace("$bte_team_co-leaders$", Integer.toString(coLeaders));
            value = value.replace("$bte_team_reviewers$", Integer.toString(reviewers));
            value = value.replace("$bte_team_builders$", Integer.toString(builders));
        }
        if (!leaderList.isEmpty()) {
            value = value.replace("$bte_team_leader_list$", String.join("\n", leaderList));
        }
        if (!coLeaderList.isEmpty()) {
            value = value.replace("$bte_team_co-leader_list$", String.join("\n", coLeaderList));
        }
        if (!reviewerList.isEmpty()) {
            value = value.replace("$bte_team_reviewer_list$", String.join("\n", reviewerList));
        }
        if (!builderList.isEmpty()) {
            value = value.replace("$bte_team_builder_list$", String.join("\n", builderList));
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            value = PlaceholderAPI.setPlaceholders(null, value);
        }
        return value;
    }

    private JSONObject getRequest(String endpoint, String key) {
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
            int code = con.getResponseCode();
            if (code < 200 || code > 299) {
                bteDiscordAddon.getLogger().warning("Request to https://buildtheearth.net/" + endpoint + " not successful. Response code: " + code);
                if (code == 401) {
                    bteDiscordAddon.getLogger().warning("Invalid API key.");
                } else if (code == 404) {
                    bteDiscordAddon.getLogger().warning(endpoint + " endpoint not found.");
                }
                return null;
            }
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            bteDiscordAddon.getLogger().warning("Unexpected exception making GET request to https://buildtheearth.net/" + endpoint);
            return null;
        }
    }

    public void reset() {
        projectLocations = -1;
        teamLocations = -1;
        pendingApplications = -1;
        websiteMembers = -1;
        leaders = -1;
        coLeaders = -1;
        reviewers = -1;
        builders = -1;
        leaderList.clear();
        coLeaderList.clear();
        reviewerList.clear();
        builderList.clear();
    }
}
