package me.vapor.discordplus.commands;

import me.vapor.discordplus.Status;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandUpdate implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("ds.admin.update") || !commandSender.isOp()) {return false;}
        Status.updateEmbed(null, false, null);
        commandSender.sendMessage("Updated");
        return true;
    }
}
