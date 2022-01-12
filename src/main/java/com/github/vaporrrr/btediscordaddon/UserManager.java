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

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    private final HashMap<UUID, User> userMap = new HashMap<>();
    private final BTEDiscordAddon bteDiscordAddon;
    private final DiscordSRV discordSRV = DiscordSRV.getPlugin();

    public UserManager() {
        this.bteDiscordAddon = BTEDiscordAddon.getPlugin();
    }

    public HashMap<UUID, User> getUserMap() {
        return userMap;
    }

    public ArrayList<String> playerList() {
        ArrayList<String> playerList = new ArrayList<>();
        HashMap<UUID, User> userMap = bteDiscordAddon.getUserManager().getUserMap();
        for (User user : userMap.values()) {
            playerList.add(format(user));
        }
        playerList.sort(String.CASE_INSENSITIVE_ORDER);
        return playerList;
    }

    private String format(User user) {
        String format = BTEDiscordAddon.config().getString("ServerStatus.NameFormat");
        UUID UUID = user.getPlayer().getUniqueId();
        format = format.replace("$player_name$", user.getPlayer().getName());
        format = format.replace("$player_name_with_afk_status$", getFormattedMinecraftUsername(user));
        String id = getDiscordIDFromUUID(UUID);
        if (id != null) {
            format = format.replace("$discord_mention$", getDiscordMentionFromID(id));
            format = format.replace("$discord_tag$", getDiscordTagFromID(id));
            format = format.replace("$discord_username$", getDiscordUsernameFromID(id));
            format = format.replace("$discord_id$", id);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            format = PlaceholderAPI.setPlaceholders(user.getPlayer(), format);
        }
        return format;
    }

    public void add(Player player) {
        userMap.put(player.getUniqueId(), new User(player, false));
        userMap.get(player.getUniqueId()).startAfkTimer(BTEDiscordAddon.config().getInt("AutoAfkInSeconds"));
    }

    public void remove(Player player) {
        userMap.get(player.getUniqueId()).cancelAfkTimer();
        userMap.remove(player.getUniqueId());
    }

    public User getUser(Player player) {
        return userMap.get(player.getUniqueId());
    }

    public void toggleAfk(Player player) {
        User user = userMap.get(player.getUniqueId());
        boolean isAfk = user.isAfk();
        setAfk(player, !isAfk);
        //No ! since in previous line afk is reversed
        if (isAfk) {
            user.startAfkTimer(BTEDiscordAddon.config().getInt("AutoAfkInSeconds"));
        } else {
            user.cancelAfkTask();
        }
    }

    public void setAfk(Player player, boolean isAfk) {
        User user = userMap.get(player.getUniqueId());
        user.setAfk(isAfk);
        if (!isAfk) {
            user.startAfkTimer(BTEDiscordAddon.config().getInt("AutoAfkInSeconds"));
        } else {
            user.cancelAfkTask();
        }
    }

    private String getDiscordTagFromID(String id) {
        github.scarsz.discordsrv.dependencies.jda.api.entities.User user = getDiscordUserFromID(id);
        if (user == null) return "";
        return user.getAsTag();
    }

    private String getDiscordUsernameFromID(String id) {
        github.scarsz.discordsrv.dependencies.jda.api.entities.User user = getDiscordUserFromID(id);
        if (user == null) return "";
        return user.getName();
    }

    private github.scarsz.discordsrv.dependencies.jda.api.entities.User getDiscordUserFromID(String id) {
        return discordSRV.getJda().getUserById(id);
    }

    private String getFormattedMinecraftUsername(User user) {
        String name = user.getPlayer().getName().replace("_", "\\_");
        return user.isAfk() ? "[AFK]" + name : name;
    }

    private String getDiscordMentionFromID(String id) {
        return "<@!" + id + ">";
    }

    private String getDiscordIDFromUUID(UUID UUID) {
        AccountLinkManager accountLinkManager = discordSRV.getAccountLinkManager();
        if (accountLinkManager == null) return null;
        return accountLinkManager.getDiscordId(UUID);
    }
}
