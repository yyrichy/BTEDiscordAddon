package github.vaporrrr.discordplus;

import github.scarsz.discordsrv.DiscordSRV;
import github.vaporrrr.discordplus.listeners.BukkitListener;
import github.vaporrrr.discordplus.listeners.DiscordListener;
import github.vaporrrr.discordplus.commands.CommandReload;
import github.vaporrrr.discordplus.commands.CommandUpdate;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordPlus extends JavaPlugin {
    private final DiscordListener discordSRVListener = new DiscordListener(this);
    private final UserManager userManager = new UserManager();
    private final ServerStatus serverStatus = new ServerStatus(this);
    @Override
    public void onEnable() {
        getLogger().info("DiscordPlus enabled!");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
        getCommand("ds-update").setExecutor(new CommandUpdate(this));
        getCommand("ds-reload").setExecutor(new CommandReload());
        DiscordSRV.api.subscribe(discordSRVListener);
    }
    public void onDisable(){
        serverStatus.update();
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }
}

