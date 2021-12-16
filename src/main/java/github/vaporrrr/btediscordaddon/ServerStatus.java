package github.vaporrrr.btediscordaddon;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
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
        format = format.replace("%username%", getFormattedMinecraftUsername(user));
        if (getDiscordIDFromUUID(UUID) != null) {
            format = format.replace("%discord-mention%", getDiscordMentionFromUUID(UUID));
            format = format.replace("%discord-tag%", getDiscordTagFromUUID(UUID));
            format = format.replace("%discord-username%", getDiscordUsernameFromUUID(UUID));
            format = format.replace("%discord-id%", getDiscordIDFromUUID(UUID));
        }
        return format;
    }

    private void editStatus(EmbedBuilder embed) {
        String channelIDPath = "ServerStatus.ChannelID";
        String messageIDPath = "ServerStatus.MessageID";
        TextChannel channel = jda.getTextChannelById(bteDiscordAddon.getConfig().getString(channelIDPath));
        if (channel == null) {
            logSevere(notFoundFormat("TextChannel", channelIDPath));
            return;
        }
        try {
            channel.editMessageById(bteDiscordAddon.getConfig().getString(messageIDPath), embed.build()).queue();
        } catch(Exception e) {
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
        return discordSRV.getAccountLinkManager().getDiscordId(UUID);
    }

    private void logSevere(String message) {
        bteDiscordAddon.getLogger().severe(message);
    }

    private String notFoundFormat(String type, String path) {
        return type + " from " + path + " does not exist.";
    }
}
