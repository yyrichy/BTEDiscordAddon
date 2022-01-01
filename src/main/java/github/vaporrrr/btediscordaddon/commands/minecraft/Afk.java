package github.vaporrrr.btediscordaddon.commands.minecraft;

import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Afk implements CommandExecutor {
    private final BTEDiscordAddon bteDiscordAddon;
    private final HashMap<UUID, Long> cooldownMap = new HashMap<>();

    public Afk(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("bted.command.afk") && !commandSender.isOp()) {
            commandSender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
            return true;
        }
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        Player player = (Player) commandSender;
        if (cooldownMap.containsKey(player.getUniqueId())) {
            long last = cooldownMap.get(player.getUniqueId());
            long cooldown = bteDiscordAddon.getConfig().getInt("MinecraftCommands.afk.CooldownInSeconds") * 1000L;
            long elapsed = System.currentTimeMillis() - last;
            if (elapsed < cooldown) {
                commandSender.sendMessage(ChatColor.RED + "Please wait " + (int) ((cooldown - elapsed) / 1000) + " seconds before using this command again.");
                return true;
            } else {
                cooldownMap.put(player.getUniqueId(), System.currentTimeMillis());
            }
        } else {
            cooldownMap.put(player.getUniqueId(), System.currentTimeMillis());
        }
        bteDiscordAddon.getUserManager().toggleAfk(player);
        bteDiscordAddon.getServerStatus().update();
        return true;
    }
}