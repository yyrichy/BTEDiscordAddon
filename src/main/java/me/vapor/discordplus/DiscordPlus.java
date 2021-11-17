package me.vapor.discordplus;

import github.scarsz.discordsrv.DiscordSRV;
import me.vapor.discordplus.commands.CommandReload;
import me.vapor.discordplus.commands.CommandUpdate;
import me.vapor.discordplus.listeners.BukkitListener;
import me.vapor.discordplus.listeners.DiscordListener;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordPlus extends JavaPlugin {
    private final DiscordListener discordsrvListener = new DiscordListener(this);
    @Override
    public void onEnable() {
        getLogger().info("DiscordPlus enabled!");
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new BukkitListener(), this);
        getCommand("ds-update").setExecutor(new CommandUpdate());
        getCommand("ds-reload").setExecutor(new CommandReload());
        DiscordSRV.api.subscribe(discordsrvListener);
        /*
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Handshake.Client.SET_PROTOCOL) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                PacketContainer packet = event.getPacket();
                getLogger().info(player.getName());
                getLogger().info(packet.getStrings().toString());
            }
        });
        */
    }
    public void onDisable(){
        Status.updateEmbed(null, true, null);
    }
}

