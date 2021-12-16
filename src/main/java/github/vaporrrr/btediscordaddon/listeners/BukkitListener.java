package github.vaporrrr.btediscordaddon.listeners;

import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import github.vaporrrr.btediscordaddon.ServerStatus;
import github.vaporrrr.btediscordaddon.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {
    private final UserManager userManager;
    private final ServerStatus serverStatus;
    public BukkitListener(BTEDiscordAddon bteDiscordAddon) {
        this.userManager = bteDiscordAddon.getUserManager();
        this.serverStatus = bteDiscordAddon.getServerStatus();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        userManager.add(event.getPlayer());
        serverStatus.update();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        userManager.remove(event.getPlayer());
        serverStatus.update();
    }
}