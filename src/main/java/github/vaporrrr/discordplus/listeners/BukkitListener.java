package github.vaporrrr.discordplus.listeners;

import github.vaporrrr.discordplus.DiscordPlus;
import github.vaporrrr.discordplus.ServerStatus;
import github.vaporrrr.discordplus.UserManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {
    private final UserManager userManager;
    private final ServerStatus serverStatus;
    public BukkitListener(DiscordPlus discordPlus) {
        this.userManager = discordPlus.getUserManager();
        this.serverStatus = discordPlus.getServerStatus();
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