package com.github.vaporrrr.btediscordaddon.listeners;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.commands.DiscordCommandManager;
import com.github.vaporrrr.btediscordaddon.schematics.Schematics;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.util.DiscordUtil;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class DiscordListener {
    private final BTEDiscordAddon bteDiscordAddon;
    private final Schematics schematics;
    private final DiscordCommandManager discordCommandManager;

    public DiscordListener(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
        this.schematics = new Schematics(bteDiscordAddon);
        this.discordCommandManager = new DiscordCommandManager(bteDiscordAddon);
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        bteDiscordAddon.getLogger().info("Discord Ready!");
        bteDiscordAddon.getServerStatus().setJDA(DiscordUtil.getJda());
        bteDiscordAddon.getServerStatus().update();
        bteDiscordAddon.startStats();
    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getId().equals(bteDiscordAddon.getConfig().getString("Schematics.Upload.ChannelID"))) {
            schematics.upload(event);
            return;
        }
        if (event.getChannel().getId().equals(bteDiscordAddon.getConfig().getString("Schematics.Download.ChannelID"))) {
            schematics.download(event);
            return;
        }
        if (event.getChannel().getId().equals(bteDiscordAddon.getConfig().getString("Linking.ChannelID"))) {
            String response = DiscordSRV.getPlugin().getAccountLinkManager().process(event.getMessage().getContentRaw(), event.getAuthor().getId());
            int delay = bteDiscordAddon.getConfig().getInt("Linking.DelayBeforeDeleteMsgInSeconds");
            if (delay <= 0) {
                event.getChannel().sendMessage(response).queue();
            } else {
                try {
                    event.getChannel().sendMessage(response).complete().delete().completeAfter(delay, TimeUnit.SECONDS);
                    event.getMessage().delete().queue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        if (!event.getMessage().getContentRaw().substring(0, 1).equals(bteDiscordAddon.getConfig().getString("DiscordCommandsPrefix"))) {
            return;
        }
        String[] args = event.getMessage().getContentRaw().split(" ");
        String command = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);
        discordCommandManager.executeCommand(event, command, args);
    }
}
