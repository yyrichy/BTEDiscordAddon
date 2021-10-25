package me.vapor.discordplus.commands;

import me.vapor.discordplus.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class CommandReload implements CommandExecutor {
    private static final Plugin plugin = Main.getPlugin(Main.class);
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("ds.admin.reload") || !commandSender.isOp()) {return false;}
        plugin.reloadConfig();
        commandSender.sendMessage("Config Reloaded");
        return true;
    }
}