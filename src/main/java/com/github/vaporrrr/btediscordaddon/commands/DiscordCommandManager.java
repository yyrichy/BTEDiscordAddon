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

package com.github.vaporrrr.btediscordaddon.commands;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.commands.discord.Linked;
import com.github.vaporrrr.btediscordaddon.commands.discord.Online;
import com.github.vaporrrr.btediscordaddon.commands.discord.Setup;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;

import java.util.HashMap;

public class DiscordCommandManager {
    private final BTEDiscordAddon bteDiscordAddon;
    private final HashMap<String, DiscordCommand> discordCommands = new HashMap<>();

    public DiscordCommandManager(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
        Linked linked = new Linked();
        discordCommands.put(linked.getName(), linked);
        Setup setup = new Setup();
        discordCommands.put(setup.getName(), setup);
        Online online = new Online();
        discordCommands.put(online.getName(), online);
    }

    public void executeCommand(DiscordGuildMessageReceivedEvent event, String command, String[] args) {
        DiscordCommand discordCommand = discordCommands.get(command);
        if (discordCommand != null) {
            if (discordCommand.hasPermission(bteDiscordAddon, event.getMember())) {
                if (discordCommand.getArguments() != null && args.length < discordCommand.getArguments().length) {
                    event.getChannel().sendMessage("Usage: " + bteDiscordAddon.getConfig().getString("DiscordCommandsPrefix") + discordCommand.getName() + " " + String.join(" ", discordCommand.getArguments())).queue();
                    return;
                }
                discordCommand.execute(bteDiscordAddon, event, args);
            }
        }
    }
}
