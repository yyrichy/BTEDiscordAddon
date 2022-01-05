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
import com.github.vaporrrr.btediscordaddon.ServerStatus;
import com.github.vaporrrr.btediscordaddon.User;
import com.github.vaporrrr.btediscordaddon.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;

public class BukkitListener implements Listener {
    private final BTEDiscordAddon bteDiscordAddon;
    private final UserManager userManager;
    private final ServerStatus serverStatus;
    private long lastCheck;

    public BukkitListener(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
        this.userManager = bteDiscordAddon.getUserManager();
        this.serverStatus = bteDiscordAddon.getServerStatus();
        this.lastCheck = System.currentTimeMillis();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        userManager.add(event.getPlayer());
        serverStatus.update();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        userManager.remove(event.getPlayer());
        serverStatus.update();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (System.currentTimeMillis() - lastCheck < 500) {
            return;
        }
        checkAndStartTimer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        checkAndStartTimer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        checkAndStartTimer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerItemBreak(PlayerItemBreakEvent event) {
        checkAndStartTimer(event.getPlayer());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkAndStartTimer(event.getPlayer());
    }

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().toLowerCase().startsWith("/afk")) {
            checkAndStartTimer(event.getPlayer());
        }
    }

    private void checkAndStartTimer(Player player) {
        int interval = bteDiscordAddon.getConfig().getInt("AutoAfkInSeconds");
        if (interval < 1) {
            return;
        }
        User user = userManager.getUser(player);
        if (user.isAfk()) {
            user.setAfk(false);
            user.cancelAfkTask();
            serverStatus.update();
        } else {
            user.startAfkTimer(interval, bteDiscordAddon.getServerStatus());
        }
        lastCheck = System.currentTimeMillis();
    }
}