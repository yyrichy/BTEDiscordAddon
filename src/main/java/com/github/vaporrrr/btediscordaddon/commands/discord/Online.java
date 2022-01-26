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

package com.github.vaporrrr.btediscordaddon.commands.discord;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.commands.DiscordCommand;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.ArrayList;

public class Online extends DiscordCommand {
    @Override
    public void execute(DiscordGuildMessageReceivedEvent event, String[] args) {
        ArrayList<String> playerList = BTEDiscordAddon.getPlugin().getUserManager().playerList();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.WHITE);
        embed.setTitle(BTEDiscordAddon.config().getOrDefault("DiscordCommands." + getName() + ".Title", "Minecraft Server"));
        if (playerList.size() == 0) {
            embed.setDescription("No Players Online");
        } else {
            embed.addField(playerList.size() + "/" + Bukkit.getMaxPlayers() + " Player(s) Online", String.join("\n", playerList), false);
        }
        event.getChannel().sendMessage(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "online";
    }

    @Override
    public String[] getArguments() {
        return null;
    }
}
