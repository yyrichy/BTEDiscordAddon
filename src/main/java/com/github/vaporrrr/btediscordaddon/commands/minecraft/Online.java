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
import github.scarsz.discordsrv.hooks.VaultHook;
import github.scarsz.discordsrv.util.PluginUtil;
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

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("bted.command.online") && !commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
        if (Bukkit.getOnlinePlayers().size() == 0) {
            commandSender.sendMessage(ChatColor.GOLD + "No players online.");
            return true;
        }
        HashMap<UUID, User> userMap = BTEDiscordAddon.getPlugin().getUserManager().getUserMap();
        if (PluginUtil.pluginHookIsEnabled("vault")) {
            HashMap<String, ArrayList<String>> playerMap = new HashMap<>();
            for (User user : userMap.values()) {
                String group = VaultHook.getPrimaryGroup(user.getPlayer());
                if (!playerMap.containsKey(group)) {
                    playerMap.put(group, new ArrayList<>());
                }
                playerMap.get(group).add((user.isAfk() ? "[AFK]" : "") + user.getPlayer().getName());
            }
            commandSender.sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + Bukkit.getOnlinePlayers().size() + " Online Players:");
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
            commandSender.sendMessage(ChatColor.BOLD + "" + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + "Online Players:");
            for (String player : playerList) {
                commandSender.sendMessage(ChatColor.GRAY + " - " + player);
            }
        }
        return true;
    }
}
