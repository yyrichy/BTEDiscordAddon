package github.vaporrrr.discordplus.commands.minecraft;

import github.vaporrrr.discordplus.DiscordPlus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Update implements CommandExecutor {
    private final DiscordPlus discordPlus;
    public Update(DiscordPlus discordPlus) {
        this.discordPlus = discordPlus;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("ds.admin.update") || !commandSender.isOp()) return false;
        discordPlus.getServerStatus().update();
        commandSender.sendMessage("Updated");
        return true;
    }
}
