package github.vaporrrr.btediscordaddon;

import github.scarsz.discordsrv.DiscordSRV;
import github.vaporrrr.btediscordaddon.commands.minecraft.Afk;
import github.vaporrrr.btediscordaddon.listeners.BukkitListener;
import github.vaporrrr.btediscordaddon.listeners.DiscordListener;
import github.vaporrrr.btediscordaddon.commands.minecraft.Reload;
import github.vaporrrr.btediscordaddon.commands.minecraft.Update;
import org.bukkit.plugin.java.JavaPlugin;

public class BTEDiscordAddon extends JavaPlugin {
    private final DiscordListener discordSRVListener = new DiscordListener(this);
    private final UserManager userManager = new UserManager();
    private final ServerStatus serverStatus = new ServerStatus(this);
    @Override
    public void onEnable() {
        getLogger().info("Enabled!");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BukkitListener(this), this);
        getCommand("ds-update").setExecutor(new Update(this));
        getCommand("ds-reload").setExecutor(new Reload(this));
        getCommand("afk").setExecutor(new Afk(this));
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

