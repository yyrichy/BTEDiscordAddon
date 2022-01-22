package com.github.vaporrrr.btediscordaddon.util;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.luckperms.LP;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Placeholder {
    private static final Pattern pattern = Pattern.compile(String.format("\\%s((?<identifier>[a-zA-Z0-9]+)_)(?<parameters>[^%s%s]+)\\%s", '%', '%', '%', '%'));

    public static String replacePlaceholders(String input, Player player) {
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            return input;
        }

        StringBuffer builder = new StringBuffer();

        do {
            final String identifier = matcher.group("identifier");
            final String parameters = matcher.group("parameters");

            if (identifier.equals("bted")) {
                String requested = request(player, parameters);
                matcher.appendReplacement(builder, requested != null ? requested : matcher.group(0));
            }
        }
        while (matcher.find());

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(builder).toString());
    }

    public static String request(Player player, String parameter) {
        switch (parameter) {
            case "time_now_unix":
                return Long.toString(System.currentTimeMillis() / 1000L);
            case "unique_players_joined":
                return Integer.toString(Bukkit.getOfflinePlayers().length);
            case "linked_players":
                return Integer.toString(DiscordSRV.getPlugin().getAccountLinkManager().getLinkedAccountCount());
            case "memory":
                Runtime r = Runtime.getRuntime();
                long usedMemory = (r.totalMemory() - r.freeMemory()) / 1048576;
                long maxMemory = r.maxMemory() / 1048576;
                float percent = ((float) usedMemory / (float) maxMemory) * 100;
                return String.format("`%.2f", percent) + "`% | `" + usedMemory + "`/`" + maxMemory + "` MB";
            case "uptime":
                long milliseconds = ManagementFactory.getRuntimeMXBean().getUptime();
                long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
                long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
                long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
                return String.format("`%d` Days `%02d` Hours `%02d` Minutes", days, hours, minutes);
        }
        if (parameter.startsWith("luckperms_group_size")) {
            String groupName = parameter.substring("luckperms_group_size_".length());
            LP lp = BTEDiscordAddon.getPlugin().getLuckPerms();
            if (lp == null) return "";
            return lp.getGroupSize(groupName) != -1 ? String.valueOf(lp.getGroupSize(groupName)) : "";
        }

        String key = BTEDiscordAddon.config().getString("Stats.Team.BTEWebsiteAPIKey");
        if (parameter.startsWith("website")) {
            if (parameter.equals("website_locations_total")) {
                JSONObject totalLocations = getRequest("map/data/locations", null);
                return applyOrEmptyString(totalLocations, Placeholder::getLength);
            }
            if (key == null || key.isEmpty()) return "";
            if (parameter.equals("website_locations_team")) {
                JSONObject locations = getRequest("api/v1/locations", key);
                return applyOrEmptyString(locations, Placeholder::getLength);
            }
            if (parameter.equals("website_applications_pending")) {
                JSONObject pending = getRequest("api/v1/applications/pending", key);
                return applyOrEmptyString(pending, Placeholder::getLength);
            }
            JSONObject members = getRequest("api/v1/members", key);
            if (members == null) return "";
            if (parameter.equals("website_members")) {
                return applyOrEmptyString(members, Placeholder::getLength);
            }
            ArrayList<String> leaderList = new ArrayList<>();
            ArrayList<String> coLeaderList = new ArrayList<>();
            ArrayList<String> reviewerList = new ArrayList<>();
            ArrayList<String> builderList = new ArrayList<>();
            ArrayList<String> memberList = new ArrayList<>();
            for (int i = 0; i < members.getJSONArray("members").length(); i++) {
                JSONObject member = members.getJSONArray("members").getJSONObject(i);
                memberList.add(member.getString("discordTag"));
                switch (member.getString("role")) {
                    case "leader":
                        leaderList.add(member.getString("discordTag"));
                        break;
                    case "co-leader":
                        coLeaderList.add(member.getString("discordTag"));
                        break;
                    case "reviewer":
                        reviewerList.add(member.getString("discordTag"));
                        break;
                    case "builder":
                        builderList.add(member.getString("discordTag"));
                        break;
                }
            }
            switch (parameter) {
                case "website_leaders":
                    return String.valueOf(leaderList.size());
                case "website_co-leaders":
                    return String.valueOf(coLeaderList.size());
                case "website_reviewers":
                    return String.valueOf(reviewerList.size());
                case "website_builders":
                    return String.valueOf(builderList.size());
                case "website_leader_list":
                    return String.join("\n", leaderList);
                case "website_co-leader_list":
                    return String.join("\n", coLeaderList);
                case "website_reviewer_list":
                    return String.join("\n", reviewerList);
                case "website_builder_list":
                    return String.join("\n", builderList);
                case "website_member_list":
                    return String.join("\n", memberList);
            }

        }

        if (parameter.startsWith("player")) {
            if (player == null) return "";
            switch (parameter) {
                case "player_name":
                    return player.getName();
                case "player_name_escape_markdown":
                    return DiscordUtil.escapeMarkdown(player.getName());
                case "player_name_display":
                    return player.getDisplayName();
                case "player_name_display_escape_markdown":
                    return DiscordUtil.escapeMarkdown(player.getDisplayName());
                case "player_UUID":
                    return String.valueOf(player.getUniqueId());
                case "player_afk_status":
                    return BTEDiscordAddon.getPlugin().getUserManager().getUser(player).isAfk() ? "[AFK]" : "";
            }

            String id = getDiscordIDFromUUID(player.getUniqueId());
            if (parameter.startsWith("player_discord")) {
                if (id == null) return "";
                if (parameter.equals("player_discord_id")) return id;

                github.scarsz.discordsrv.dependencies.jda.api.entities.User discordUser = getDiscordUserFromID(id);
                if (discordUser == null) return "";
                switch (parameter) {
                    case "player_discord_name":
                        return discordUser.getName();
                    case "player_discord_tag":
                        return discordUser.getAsTag();
                }

                Member member = DiscordSRV.getPlugin().getMainGuild().getMember(discordUser);
                if (member == null) return "";
                switch (parameter) {
                    case "player_discord_creation_unix":
                        return Long.toString(member.getTimeCreated().toEpochSecond());
                    case "player_discord_creation_date":
                        return formatDateOrEmptyString(member.getTimeCreated());
                    case "player_discord_join_unix":
                        return applyOrEmptyString(member.getTimeJoined(), Placeholder::getTimeUnix);
                    case "player_discord_join_date":
                        return formatDateOrEmptyString(member.getTimeJoined());
                    case "player_discord_boost_unix":
                        return applyOrEmptyString(member.getTimeBoosted(), Placeholder::getTimeUnix);
                    case "player_discord_boost_date":
                        return formatDateOrEmptyString(member.getTimeBoosted());
                    case "player_discord_mention":
                        return member.getAsMention();
                    case "player_discord_name_effective":
                        return member.getEffectiveName();
                    case "player_discord_nickname":
                        return orEmptyString(member.getNickname());
                    case "player_discord_status":
                        return member.getOnlineStatus().getKey();
                    case "player_discord_game_name":
                        return member.getActivities().stream().findFirst().map(Activity::getName).orElse("");
                    case "player_discord_game_url":
                        return member.getActivities().stream().findFirst().map(Activity::getUrl).orElse("");
                }

                if (member.getRoles().isEmpty()) return "";
                Role role = member.getRoles().get(0);
                switch (parameter) {
                    case "player_discord_role_id":
                        return role.getId();
                    case "player_discord_role_name":
                        return role.getName();
                    case "player_discord_role_mention":
                        return role.getAsMention();
                    case "player_discord_role_color_hex":
                        return applyOrEmptyString(role.getColor(), Placeholder::getHex);
                }
            }
        }

        Guild mainGuild = DiscordSRV.getPlugin().getMainGuild();
        if (parameter.startsWith("guild")) {
            if (mainGuild == null) return "";
            switch (parameter) {
                case "guild_name":
                    return mainGuild.getName();
                case "guild_id":
                    return mainGuild.getId();
                case "guild_description":
                    return orEmptyString(mainGuild.getDescription());
                case "guild_creation_unix":
                    return Long.toString(mainGuild.getTimeCreated().toEpochSecond());
                case "guild_creation_date":
                    return formatDateOrEmptyString(mainGuild.getTimeCreated());
                case "guild_banner_id":
                    return orEmptyString(mainGuild.getBannerId());
                case "guild_banner_url":
                    return orEmptyString(mainGuild.getBannerUrl());
                case "guild_icon_id":
                    return orEmptyString(mainGuild.getIconId());
                case "guild_icon_url":
                    return orEmptyString(mainGuild.getIconUrl());
                case "guild_splash_id":
                    return orEmptyString(mainGuild.getSplashId());
                case "guild_splash_url":
                    return orEmptyString(mainGuild.getSplashUrl());
                case "guild_region_name":
                    return mainGuild.getRegion().getName();
                case "guild_region_emoji":
                    return mainGuild.getRegion().getEmoji();
                case "guild_region_key":
                    return mainGuild.getRegion().getKey();
                case "guild_vanity_code":
                    return orEmptyString(mainGuild.getVanityCode());
                case "guild_vanity_url":
                    return orEmptyString(mainGuild.getVanityUrl());
                case "guild_members":
                    return Integer.toString(mainGuild.getMemberCount());
                case "guild_member_max":
                    return Integer.toString(mainGuild.getMaxMembers());
                case "guild_categories":
                    return Integer.toString(mainGuild.getCategories().size());
                case "guild_channel_voice":
                    return Integer.toString(mainGuild.getVoiceChannels().size());
                case "guild_channel_text":
                    return Integer.toString(mainGuild.getTextChannels().size());
                case "guild_channel_store":
                    return Integer.toString(mainGuild.getStoreChannels().size());
                case "guild_channels":
                    return Integer.toString(mainGuild.getChannels().size());
                case "guild_roles":
                    return Integer.toString(mainGuild.getRoles().size());
                case "guild_emotes":
                    return Integer.toString(mainGuild.getEmotes().size());
                case "guild_emote_max":
                    return Integer.toString(mainGuild.getMaxEmotes());
                case "guild_boosts":
                    return Integer.toString(mainGuild.getBoostCount());
                case "guild_boosters":
                    return Integer.toString(mainGuild.getBoosters().size());
                case "guild_owner_id":
                    return mainGuild.getOwnerId();
            }
            if (parameter.startsWith("guild_role_size")) {
                String roleName = parameter.substring("guild_role_size_".length());
                List<Role> roles = mainGuild.getRolesByName(roleName, true);
                if (roles.isEmpty()) return "";
                if (roles.size() > 1)
                    BTEDiscordAddon.warn("There are multiple roles called " + roleName + ", totalling all of their members.");
                return String.valueOf(mainGuild.getMembersWithRoles(roles).size());
            }
            if (parameter.startsWith("guild_emoji")) {
                String emojiName = parameter.substring("guild_emoji_".length());
                List<Emote> emotes = mainGuild.getEmotesByName(emojiName, true);
                if (emotes.isEmpty()) return "";
                if (emotes.size() > 1)
                    BTEDiscordAddon.warn("There are multiple emotes called " + emotes + ", using the first one: " + emotes.get(0).getAsMention());
                return emotes.get(0).getAsMention();
            }
            Member owner = mainGuild.getOwner();
            if (parameter.startsWith("guild_owner")) {
                if (owner == null) return "";
                switch (parameter) {
                    case "guild_owner_name":
                        return owner.getUser().getName();
                    case "guild_owner_tag":
                        return owner.getUser().getAsTag();
                    case "guild_owner_creation_unix":
                        return Long.toString(owner.getTimeCreated().toEpochSecond());
                    case "guild_owner_creation_date":
                        return formatDateOrEmptyString(owner.getTimeCreated());
                    case "guild_owner_join_unix":
                        return applyOrEmptyString(owner.getTimeJoined(), Placeholder::getTimeUnix);
                    case "guild_owner_join_date":
                        return formatDateOrEmptyString(owner.getTimeJoined());
                    case "guild_owner_boost_unix":
                        return applyOrEmptyString(owner.getTimeBoosted(), Placeholder::getTimeUnix);
                    case "guild_owner_boost_date":
                        return formatDateOrEmptyString(owner.getTimeBoosted());
                    case "guild_owner_mention":
                        return owner.getAsMention();
                    case "guild_owner_name_effective":
                        return owner.getEffectiveName();
                    case "guild_owner_nickname":
                        return orEmptyString(owner.getNickname());
                    case "guild_owner_status":
                        return owner.getOnlineStatus().getKey();
                    case "guild_owner_game_name":
                        return owner.getActivities().stream().findFirst().map(Activity::getName).orElse("");
                    case "guild_owner_game_url":
                        return owner.getActivities().stream().findFirst().map(Activity::getUrl).orElse("");
                }
            }
        }
        return null;
    }

    private static String getLength(JSONObject object) {
        return String.valueOf(object.getJSONArray(object.keySet().iterator().next()).length());
    }

    private static String formatDateOrEmptyString(OffsetDateTime date) {
        if (date == null) return "";
        return "UTC" + date.getOffset() + " " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date(date.toInstant().toEpochMilli()));
    }

    private static github.scarsz.discordsrv.dependencies.jda.api.entities.User getDiscordUserFromID(String id) {
        return DiscordSRV.getPlugin().getJda().getUserById(id);
    }

    private static String getDiscordIDFromUUID(UUID UUID) {
        return DiscordSRV.getPlugin().getAccountLinkManager().getDiscordIdBypassCache(UUID);
    }

    private static String getTimeUnix(OffsetDateTime date) {
        return Long.toString(date.toEpochSecond());
    }

    private static JSONObject getRequest(String endpoint, String key) {
        try {
            String URL = "https://buildtheearth.net/" + endpoint;
            java.net.URL url = new URL(URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            if (key != null) {
                con.setRequestProperty("Authorization", "Bearer " + key);
            }
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
            con.setRequestMethod("GET");
            int code = con.getResponseCode();
            if (code < 200 || code > 299) {
                BTEDiscordAddon.warn("Request to https://buildtheearth.net/" + endpoint + " not successful. Response code: " + code);
                if (code == 401) {
                    BTEDiscordAddon.warn("Invalid API key.");
                } else if (code == 404) {
                    BTEDiscordAddon.warn(endpoint + " endpoint not found.");
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
            BTEDiscordAddon.severe("Unexpected exception making GET request to https://buildtheearth.net/" + endpoint);
            e.printStackTrace();
            return null;
        }
    }

    /*
        Taken from DiscordSRV's PlaceholderAPIExpansion
    */
    private static <T> String applyOrEmptyString(T input, Function<T, String> function) {
        if (input == null) return "";
        String output = function.apply(input);
        return orEmptyString(output);
    }

    private static String orEmptyString(String input) {
        return StringUtils.isNotBlank(input) ? input : "";
    }

    private static String getHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    /*
        Taken from DiscordSRV's PlaceholderUtil
     */
    public static String replacePlaceholdersToDiscord(String input) {
        return replacePlaceholdersToDiscord(input, null);
    }

    public static String replacePlaceholdersToDiscord(String input, Player player) {
        input = input.replace("&", "&\u200B");

        input = replacePlaceholders(input, player);

        input = MessageUtil.stripLegacySectionOnly(input); // Color codes will be in this form
        input = input.replace("&\u200B", "&");

        return input;
    }
}
