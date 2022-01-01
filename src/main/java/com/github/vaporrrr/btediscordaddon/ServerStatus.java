package com.github.vaporrrr.btediscordaddon;

import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;

public class ServerStatus {
    private final BTEDiscordAddon bteDiscordAddon;
    private JDA jda;
    private final Logger logger;

    public ServerStatus(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
        this.logger = bteDiscordAddon.getLogger();
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    public void update() {
        FileConfiguration config = bteDiscordAddon.getConfig();
        ArrayList<String> playerList = bteDiscordAddon.getUserManager().playerList();
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

    private void editStatus(EmbedBuilder embed) {
        String channelIDPath = "ServerStatus.ChannelID";
        String messageIDPath = "ServerStatus.MessageID";
        TextChannel channel = jda.getTextChannelById(bteDiscordAddon.getConfig().getString(channelIDPath));
        if (channel == null) {
            logSevere("TextChannel from " + channelIDPath + " does not exist.");
            return;
        }
        try {
            channel.editMessageById(bteDiscordAddon.getConfig().getString(messageIDPath), embed.build()).queue();
        } catch (Exception e) {
            logSevere("Could not edit message " + messageIDPath + " in #" + channel.getName());
            e.printStackTrace();
        }
    }

    private void logSevere(String message) {
        bteDiscordAddon.getLogger().severe(message);
    }
}
