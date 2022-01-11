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

package com.github.vaporrrr.btediscordaddon.listeners;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.commands.DiscordCommandManager;
import com.github.vaporrrr.btediscordaddon.schematics.Schematics;
import de.leonhard.storage.Config;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.util.DiscordUtil;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DiscordListener {
    private final BTEDiscordAddon bteDiscordAddon;
    private final Schematics schematics;
    private final DiscordCommandManager discordCommandManager;

    public DiscordListener(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
        this.schematics = new Schematics(bteDiscordAddon);
        this.discordCommandManager = new DiscordCommandManager(bteDiscordAddon);
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        bteDiscordAddon.info("Discord Ready!");
        bteDiscordAddon.getServerStatus().setJDA(DiscordUtil.getJda());
        bteDiscordAddon.getServerStatus().update();
        bteDiscordAddon.startStats();
    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        Config config = bteDiscordAddon.config();
        if (event.getChannel().getId().equals(config.getString("Schematics.Upload.ChannelID"))) {
            schematics.upload(event);
            return;
        }
        if (event.getChannel().getId().equals(config.getString("Schematics.Download.ChannelID"))) {
            schematics.download(event);
            return;
        }
        if (event.getChannel().getId().equals(config.getString("Linking.ChannelID"))) {
            String response = DiscordSRV.getPlugin().getAccountLinkManager().process(event.getMessage().getContentRaw(), event.getAuthor().getId());
            int delay = config.getInt("Linking.DelayBeforeDeleteMsgInSeconds");
            if (delay <= 0) {
                event.getChannel().sendMessage(response).queue();
            } else {
                try {
                    event.getChannel().sendMessage(response).complete().delete().completeAfter(delay, TimeUnit.SECONDS);
                    event.getMessage().delete().queue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        String content = event.getMessage().getContentRaw();
        if (content.length() < 2) return;
        if (!content.substring(0, 1).equals(config.getString("DiscordCommandsPrefix"))) {
            return;
        }
        String[] args = content.split(" ");
        String command = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);
        discordCommandManager.executeCommand(event, command, args);
    }
}
