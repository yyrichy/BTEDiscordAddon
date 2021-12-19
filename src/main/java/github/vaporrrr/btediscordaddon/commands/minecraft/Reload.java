package github.vaporrrr.btediscordaddon.commands.minecraft;

import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
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
        if (!commandSender.hasPermission("ds.admin.reload") || !commandSender.isOp()) return false;
        bteDiscordAddon.reloadConfig();
        commandSender.sendMessage("Config Reloaded");
        return true;
    }
}