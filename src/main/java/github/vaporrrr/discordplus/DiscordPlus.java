package github.vaporrrr.discordplus;

import github.scarsz.discordsrv.DiscordSRV;
import github.vaporrrr.discordplus.listeners.BukkitListener;
import github.vaporrrr.discordplus.listeners.DiscordListener;
import github.vaporrrr.discordplus.commands.minecraft.Reload;
import github.vaporrrr.discordplus.commands.minecraft.Update;
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
        getCommand("ds-update").setExecutor(new Update(this));
        getCommand("ds-reload").setExecutor(new Reload());
        DiscordSRV.api.subscribe(discordSRVListener);
    }
    public void onDisable(){
        serverStatus.shutdown();
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public ServerStatus getServerStatus() {
        return serverStatus;
    }
}

