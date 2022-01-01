package com.github.vaporrrr.btediscordaddon.commands.minecraft;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    private final BTEDiscordAddon bteDiscordAddon;

    public Reload(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("bted.admin.reload") && !commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
        bteDiscordAddon.reloadConfig();
        bteDiscordAddon.getServerStatus().update();
        bteDiscordAddon.restartStats();
        commandSender.sendMessage("Config reloaded, Server Status updated, Stats restarted.");
        return true;
    }
}