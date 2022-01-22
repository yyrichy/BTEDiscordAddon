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
import de.leonhard.storage.Config;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;

public abstract class DiscordCommand {
    protected boolean hasPermission(Member member) {
        Config config = BTEDiscordAddon.config();
        if (!config.getBoolean("DiscordCommands." + getName() + ".Enabled")) return false;
        return config.getStringList("DiscordCommands." + getName() + ".Permissions.Roles").stream().anyMatch(id -> member.getRoles().stream().anyMatch(r -> r.getId().equals(id))) || config.getStringList("DiscordCommands." + getName() + ".Permissions.Users").stream().anyMatch(id -> member.getId().equals(id));
    }

    public abstract void execute(DiscordGuildMessageReceivedEvent event, String[] args);

    public abstract String getName();

    public abstract String[] getArguments();
}