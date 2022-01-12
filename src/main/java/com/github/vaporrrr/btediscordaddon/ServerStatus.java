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

import com.github.vaporrrr.btediscordaddon.util.MessageUtil;
import de.leonhard.storage.Config;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.ArrayList;

public class ServerStatus {
    public void update() {
        Config config = BTEDiscordAddon.config();
        ArrayList<String> playerList = BTEDiscordAddon.getPlugin().getUserManager().playerList();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(config.getString("ServerStatus.Title"), null, config.getString("ServerStatus.IconURL"));
        if (playerList.size() == 0) {
            embed.setDescription("No Players Online.");
            embed.setColor(Color.decode("#" + config.getString("ServerStatus.Colors.NoPlayersOnline")));
        } else {
            embed.addField(playerList.size() + "/" + Bukkit.getMaxPlayers() + " Player(s) Online", String.join("\n", playerList), false);
            embed.setColor(Color.decode("#" + config.getString("ServerStatus.Colors.PlayersOnline")));
        }
        MessageUtil.editMessageFromConfig("ServerStatus.ChannelID", "ServerStatus.MessageID", embed.build(), "ServerStatus Update");
    }

    public void shutdown() {
        Config config = BTEDiscordAddon.config();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor(config.getString("ServerStatus.Title"), null, config.getString("ServerStatus.IconURL"));
        embed.addField("Server Offline", config.getString("ServerStatus.OfflineMessage"), false);
        embed.setColor(Color.decode("#" + config.getString("ServerStatus.Colors.Offline")));
        MessageUtil.editMessageFromConfig("ServerStatus.ChannelID", "ServerStatus.MessageID", embed.build(), "ServerStatus Shutdown");
    }
}
