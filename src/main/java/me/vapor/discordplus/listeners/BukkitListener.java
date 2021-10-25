package me.vapor.discordplus.listeners;

import com.earth2me.essentials.Essentials;
import me.vapor.discordplus.Main;
import me.vapor.discordplus.Status;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class BukkitListener implements Listener {
    static Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");
    private static final Plugin plugin = Main.getPlugin(Main.class);
    private static Map<String, Boolean> hasQuit = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        hasQuit.remove(event.getPlayer().getName());
        Status.updateEmbed(null, false, null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        hasQuit.put(event.getPlayer().getName(), true);
        Status.updateEmbed(event,false, null);
    }

    @EventHandler
    public void onChange(AfkStatusChangeEvent e) {
        Player player = Bukkit.getPlayer(e.getAffected().getName());
        if(player != null && player.isOnline() && !hasQuit.containsKey(e.getAffected().getName())){
            plugin.getLogger().info(e.getAffected().getName() + " is now " + (e.getValue() ? "afk." : "not afk."));
            Status.updateEmbed(null, false, e);
        }
    }
}