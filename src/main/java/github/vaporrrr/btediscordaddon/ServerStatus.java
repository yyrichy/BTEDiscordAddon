package github.vaporrrr.btediscordaddon;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerStatus {
    private final BTEDiscordAddon bteDiscordAddon;
    private final DiscordSRV discordSRV = DiscordSRV.getPlugin();
    private JDA jda;
    private final Logger logger;

    public ServerStatus(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
        this.logger = bteDiscordAddon.getLogger();
    }

    public void update() {
        FileConfiguration config = bteDiscordAddon.getConfig();
        ArrayList<String> playerList = playerList();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(config.getString("ServerStatus.Title"), null, config.getString("ServerStatus.IconURL"));
        if (playerList.size() == 0) {
            embed.setDescription("No Players Online.");
            embed.setColor(Color.decode("#" + config.getString("ServerStatus.Colors.NoPlayersOnline")));
        } else {
            embed.addField(playerList.size() + "/" + Bukkit.getMaxPlayers() + " Player(s) Online", String.join("\n", playerList), false);
            embed.setColor(Color.decode("#" + config.getString("ServerStatus.Colors.PlayersOnline")));
        }
        editStatus(embed);
    }

    public void shutdown() {
        FileConfiguration config = bteDiscordAddon.getConfig();
        TextChannel channel = jda.getTextChannelById(config.getString("ServerStatus.ChannelID"));
        if (channel == null) {
            logger.severe("Could not find TextChannel from ServerStatus.ChannelID");
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(config.getString("ServerStatus.Title"), null, config.getString("ServerStatus.IconURL"));
        embed.addField("Server Offline", config.getString("ServerStatus.OfflineMessage"), false);
        embed.setColor(Color.decode("#" + config.getString("ServerStatus.Colors.Offline")));
        editStatus(embed);
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    private ArrayList<String> playerList() {
        ArrayList<String> playerList = new ArrayList<>();
        HashMap<UUID, User> userMap = bteDiscordAddon.getUserManager().getUserMap();
        for (User user : userMap.values()) {
            playerList.add(format(user));
        }
        playerList.sort(String.CASE_INSENSITIVE_ORDER);
        return playerList;
    }

    private String format(User user) {
        String format = bteDiscordAddon.getConfig().getString("ServerStatus.NameFormat");
        UUID UUID = user.getPlayer().getUniqueId();
        format = format.replace("%player_name%", getFormattedMinecraftUsername(user));
        format = format.replace("%player_name_with_afk_status", user.getPlayer().getName());
        if (getDiscordIDFromUUID(UUID) != null) {
            format = format.replace("%btedaddon_user_mention%", getDiscordMentionFromUUID(UUID));
            format = format.replace("%btedaddon_user_tag%", getDiscordTagFromUUID(UUID));
            format = format.replace("%btedaddon_user_username%", getDiscordUsernameFromUUID(UUID));
            format = format.replace("%btedaddon_user_id%", getDiscordIDFromUUID(UUID));
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            format = PlaceholderAPI.setPlaceholders(user.getPlayer(), format);
        }
        return format;
    }

    private void editStatus(EmbedBuilder embed) {
        String channelIDPath = "ServerStatus.ChannelID";
        String messageIDPath = "ServerStatus.MessageID";
        TextChannel channel = jda.getTextChannelById(bteDiscordAddon.getConfig().getString(channelIDPath));
        if (channel == null) {
            logSevere("TextChanel from " + channelIDPath + " does not exist.");
            return;
        }
        try {
            channel.editMessageById(bteDiscordAddon.getConfig().getString(messageIDPath), embed.build()).queue();
        } catch (Exception e) {
            logSevere("Could not edit message " + messageIDPath + " in #" + channel.getName());
            e.printStackTrace();
        }
    }

    private String getDiscordTagFromUUID(UUID UUID) {
        github.scarsz.discordsrv.dependencies.jda.api.entities.User user = getDiscordUserFromUUID(UUID);
        if (user == null) return "";
        return user.getAsTag();
    }

    private String getDiscordUsernameFromUUID(UUID UUID) {
        github.scarsz.discordsrv.dependencies.jda.api.entities.User user = getDiscordUserFromUUID(UUID);
        if (user == null) return "";
        return user.getName();
    }

    private github.scarsz.discordsrv.dependencies.jda.api.entities.User getDiscordUserFromUUID(UUID UUID) {
        return discordSRV.getJda().getUserById(getDiscordIDFromUUID(UUID));
    }

    private String getFormattedMinecraftUsername(User user) {
        String name = user.getPlayer().getName().replace("_", "\\_");
        return user.isAfk() ? "[AFK]" + name : name;
    }

    private String getDiscordMentionFromUUID(UUID UUID) {
        return getDiscordIDFromUUID(UUID) == null ? "" : "<@!" + getDiscordIDFromUUID(UUID) + ">";
    }

    private String getDiscordIDFromUUID(UUID UUID) {
        AccountLinkManager accountLinkManager = discordSRV.getAccountLinkManager();
        if (accountLinkManager == null) return null;
        return accountLinkManager.getDiscordId(UUID);
    }

    private void logSevere(String message) {
        bteDiscordAddon.getLogger().severe(message);
    }
}
