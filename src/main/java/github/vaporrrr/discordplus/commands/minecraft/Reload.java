package github.vaporrrr.discordplus.commands.minecraft;

import github.vaporrrr.discordplus.DiscordPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class Reload implements CommandExecutor {
    private static final Plugin plugin = DiscordPlus.getPlugin(DiscordPlus.class);
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("ds.admin.reload") || !commandSender.isOp()) return false;
        plugin.reloadConfig();
        commandSender.sendMessage("Config Reloaded");
        return true;
    }
}