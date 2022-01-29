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

package com.github.vaporrrr.btediscordaddon.commands.minecraft;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.User;
import github.scarsz.discordsrv.util.PluginUtil;
import lombok.SneakyThrows;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Online implements CommandExecutor {

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("bted.command.online") && !commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
        HashMap<UUID, User> userMap = BTEDiscordAddon.getPlugin().getUserManager().getUserMap();
        int onlinePlayers = userMap.size();
        if (onlinePlayers == 0) {
            commandSender.sendMessage(ChatColor.GOLD + "No players online.");
            return true;
        }
        if (PluginUtil.pluginHookIsEnabled("vault")) {
            Permission permissionProvider = (Permission) Bukkit.getServer().getServicesManager().getRegistration(Class.forName("net.milkbowl.vault.permission.Permission")).getProvider();
            if (permissionProvider == null) {
                commandSender.sendMessage(ChatColor.RED + "Failed to get the registered service provider for Vault");
                return true;
            }
            HashMap<String, ArrayList<String>> playerMap = new HashMap<>();
            for (User user : userMap.values()) {
                String group = permissionProvider.getPrimaryGroup(user.getPlayer());
                if (!playerMap.containsKey(group)) {
                    playerMap.put(group, new ArrayList<>());
                }
                playerMap.get(group).add((user.isAfk() ? "[AFK]" : "") + user.getPlayer().getName());
            }
            commandSender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + onlinePlayers + " Online Players:");
            for (Map.Entry<String, ArrayList<String>> entry : playerMap.entrySet()) {
                entry.getValue().sort(String.CASE_INSENSITIVE_ORDER);
                commandSender.sendMessage(ChatColor.GOLD + entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1).toLowerCase() + " (" + entry.getValue().size() + "):");
                for (String player : entry.getValue()) {
                    commandSender.sendMessage(ChatColor.GRAY + " - " + player);
                }
            }
        } else {
            ArrayList<String> playerList = new ArrayList<>();
            for (User user : userMap.values()) {
                playerList.add((user.isAfk() ? "[AFK]" : "") + user.getPlayer().getName());
            }
            playerList.sort(String.CASE_INSENSITIVE_ORDER);
            commandSender.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + onlinePlayers + " Online Players:");
            for (String player : playerList) {
                commandSender.sendMessage(ChatColor.GRAY + " - " + player);
            }
        }
        return true;
    }
}
