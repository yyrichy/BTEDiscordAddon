/*
 * BTEDiscordAddon
 * Copyright 2022 (C) vaporrrr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.vaporrrr.btediscordaddon;

import de.leonhard.storage.Config;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.ArrayList;

public class ServerStatus {
    private final BTEDiscordAddon bteDiscordAddon;
    private JDA jda;

    public ServerStatus() {
        this.bteDiscordAddon = BTEDiscordAddon.getPlugin();
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }

    public void update() {
        Config config = BTEDiscordAddon.config();
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
        Config config = BTEDiscordAddon.config();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(config.getString("ServerStatus.Title"), null, config.getString("ServerStatus.IconURL"));
        embed.addField("Server Offline", config.getString("ServerStatus.OfflineMessage"), false);
        embed.setColor(Color.decode("#" + config.getString("ServerStatus.Colors.Offline")));
        editStatus(embed);
    }

    private void editStatus(EmbedBuilder embed) {
        String channelIDPath = "ServerStatus.ChannelID";
        String messageIDPath = "ServerStatus.MessageID";
        TextChannel channel = jda.getTextChannelById(BTEDiscordAddon.config().getString(channelIDPath));
        if (channel == null) {
            BTEDiscordAddon.severe("TextChannel from " + channelIDPath + " does not exist.");
            return;
        }
        channel.retrieveMessageById(BTEDiscordAddon.config().getString(messageIDPath)).queue((message) -> message.editMessage(embed.build()).queue(), (failure) -> BTEDiscordAddon.severe("Could not edit message " + messageIDPath + " in #" + channel.getName()));
    }
}
