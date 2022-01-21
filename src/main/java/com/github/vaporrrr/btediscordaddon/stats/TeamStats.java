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

package com.github.vaporrrr.btediscordaddon.stats;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.util.MessageUtil;
import com.github.vaporrrr.btediscordaddon.util.Placeholder;
import de.leonhard.storage.Config;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.util.PlaceholderUtil;

import java.util.TimerTask;

public class TeamStats extends TimerTask {
    private final EmbedBuilder embed = new EmbedBuilder();

    @Override
    public void run() {
        Config config = BTEDiscordAddon.config();
        embed.clear();
        embed.setTitle("Team Statistics");
        for (String value : config.getStringList("Stats.Team.Description")) {
            add(format(value));
        }
        embed.setFooter("Updated every " + config.getInt("Stats.Team.IntervalInSeconds") + " seconds");
        MessageUtil.editMessageFromConfig("Stats.Team.ChannelID", "Stats.Team.MessageID", embed.build(), "TeamStats");
    }

    private void add(String value) {
        embed.appendDescription("\n" + value);
    }

    private String format(String value) {
        value = Placeholder.replacePlaceholdersToDiscord(value);
        value = PlaceholderUtil.replacePlaceholdersToDiscord(value);
        return value;
    }
}
