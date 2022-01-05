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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

public class User {
    private final Player player;
    private boolean isAfk;
    private final Timer t = new Timer();
    private TimerTask afkTimerTask = new TimerTask() {
        @Override
        public void run() {
            setAfk(true);
        }
    };

    public User(Player player, boolean isAfk) {
        this.player = player;
        this.isAfk = isAfk;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isAfk() {
        return isAfk;
    }

    public void setAfk(boolean isAfk) {
        this.isAfk = isAfk;
        player.sendMessage(ChatColor.GRAY + "You are " + (isAfk ? "now" : "no longer") + " afk.");
        notifyEveryone();
    }

    public void cancelAfkTimer() {
        t.cancel();
        afkTimerTask.cancel();
    }

    public void cancelAfkTask() {
        afkTimerTask.cancel();
    }

    public void startAfkTimer(int interval, ServerStatus serverStatus) {
        if (interval < 1) return;
        afkTimerTask.cancel();
        afkTimerTask = new TimerTask() {
            @Override
            public void run() {
                setAfk(true);
                serverStatus.update();
            }
        };
        t.schedule(afkTimerTask, interval * 1000L);
    }

    private void notifyEveryone() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getUniqueId().equals(player.getUniqueId())) {
                p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.GRAY + " is " + (isAfk ? "now" : "no longer") + " afk");
            }
        }
    }
}
