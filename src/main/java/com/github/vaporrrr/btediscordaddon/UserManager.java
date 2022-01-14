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

import com.github.vaporrrr.btediscordaddon.util.MessageUtil;
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
    private final DiscordSRV discordSRV = DiscordSRV.getPlugin();
    private final ServerStatus serverStatus = BTEDiscordAddon.getPlugin().getServerStatus();

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
        UUID UUID = user.getPlayer().getUniqueId();
        format = format.replace("$player_name$", MessageUtil.escapeMarkdown(user.getPlayer().getName()));
        format = format.replace("$player_name_with_afk_status$", MessageUtil.escapeMarkdown(getFormattedMinecraftUsername(user)));
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
        if (hasAfkAutoPermission(player)) {
            userMap.get(player.getUniqueId()).startAfkTimer(BTEDiscordAddon.config().getInt("AutoAfkInSeconds"));
        }
        serverStatus.update();
    }

    public void remove(Player player) {
        userMap.get(player.getUniqueId()).cancelAfkTimer();
        userMap.remove(player.getUniqueId());
        serverStatus.update();
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
        serverStatus.update();
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
        return (user.isAfk() ? "[AFK]" : "") + user.getPlayer().getName();
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
