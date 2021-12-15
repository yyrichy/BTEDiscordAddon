package github.vaporrrr.btediscordaddon.commands.minecraft;

import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Update implements CommandExecutor {
    private final BTEDiscordAddon BTEDiscordAddon;
    public Update(BTEDiscordAddon BTEDiscordAddon) {
        this.BTEDiscordAddon = BTEDiscordAddon;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("ds.admin.update") || !commandSender.isOp()) return false;
        BTEDiscordAddon.getServerStatus().update();
        commandSender.sendMessage("Updated");
        return true;
    }
}
