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

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.commands.DiscordCommand;

public class Setup extends DiscordCommand {
    @Override
    public void execute(BTEDiscordAddon bteDiscordAddon, DiscordGuildMessageReceivedEvent event, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Minecraft Server Status will be edited into this message. Type " + bteDiscordAddon.getConfig().getString("DiscordCommandsPrefix") + getName() + " again to change the location.");
        embed.setFooter("Updating...");
        event.getChannel().sendMessage(embed.build()).queue((m -> {
            bteDiscordAddon.getConfig().set("ServerStatus.ChannelID", m.getTextChannel().getId());
            bteDiscordAddon.getConfig().set("ServerStatus.MessageID", m.getId());
            bteDiscordAddon.saveConfig();
            bteDiscordAddon.getServerStatus().update();
        }));
    }

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public String[] getArguments() {
        return null;
    }
}
