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

import com.github.vaporrrr.btediscordaddon.util.Placeholder;
import github.scarsz.discordsrv.util.PlaceholderUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    private final HashMap<UUID, User> userMap = new HashMap<>();

    public HashMap<UUID, User> getUserMap() {
        return userMap;
    }

    public ArrayList<String> playerList() {
        ArrayList<String> playerList = new ArrayList<>();
        HashMap<UUID, User> userMap = BTEDiscordAddon.getPlugin().getUserManager().getUserMap();
        for (User user : userMap.values()) {
            playerList.add(format(user));
        }
        playerList.sort(String.CASE_INSENSITIVE_ORDER);
        return playerList;
    }

    private String format(User user) {
        String format = BTEDiscordAddon.config().getString("ServerStatus.NameFormat");
        Player player = user.getPlayer();
        format = Placeholder.replacePlaceholdersToDiscord(format, player);
        format = PlaceholderUtil.replacePlaceholdersToDiscord(format, player);
        return format;
    }

    public void add(Player player) {
        userMap.put(player.getUniqueId(), new User(player, false));
        if (hasAfkAutoPermission(player)) {
            userMap.get(player.getUniqueId()).startAfkTimer(BTEDiscordAddon.config().getInt("AutoAfkInSeconds"));
        }
        BTEDiscordAddon.getPlugin().getServerStatus().update();
    }

    public void remove(Player player) {
        userMap.get(player.getUniqueId()).cancelAfkTimer();
        userMap.remove(player.getUniqueId());
        BTEDiscordAddon.getPlugin().getServerStatus().update();
    }

    public User getUser(Player player) {
        return userMap.get(player.getUniqueId());
    }

    public boolean hasAfkAutoPermission(Player player) {
        return player.hasPermission("bted.afkauto");
    }

    public void toggleAfk(Player player) {
        User user = userMap.get(player.getUniqueId());
        setAfk(user, !user.isAfk());
    }

    public void setAfk(User user, boolean isAfk) {
        user.setAfk(isAfk);
        if (!isAfk) {
            if (hasAfkAutoPermission(user.getPlayer())) {
                user.startAfkTimer(BTEDiscordAddon.config().getInt("AutoAfkInSeconds"));
            }
        } else {
            user.cancelAfkTask();
        }
        BTEDiscordAddon.getPlugin().getServerStatus().update();
    }
}
