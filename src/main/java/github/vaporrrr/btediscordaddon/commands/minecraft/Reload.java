package github.vaporrrr.btediscordaddon.commands.minecraft;

import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {
    private final BTEDiscordAddon BTEDiscordAddon;
    public Reload(BTEDiscordAddon BTEDiscordAddon) {
        this.BTEDiscordAddon = BTEDiscordAddon;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("ds.admin.reload") || !commandSender.isOp()) return false;
        BTEDiscordAddon.reloadConfig();
        commandSender.sendMessage("Config Reloaded");
        return true;
    }
}