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
    public Afk(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
    }
    private final HashMap<UUID, Long> cooldownMap = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!commandSender.hasPermission("ds.command.afk") || !commandSender.isOp()) return false;
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        Player player = (Player) commandSender;
        if (cooldownMap.containsKey(player.getUniqueId())) {
            long last = cooldownMap.get(player.getUniqueId());
            long cooldown = 5000;
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
        commandSender.sendMessage(ChatColor.GRAY + "You are now " + (bteDiscordAddon.getUserManager().getUser(player).isAfk() ? "" : "not ") + "afk.");
        bteDiscordAddon.getServerStatus().update();
        return true;
    }
}