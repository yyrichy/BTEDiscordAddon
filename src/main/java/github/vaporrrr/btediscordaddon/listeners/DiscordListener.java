package github.vaporrrr.btediscordaddon.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import github.vaporrrr.btediscordaddon.commands.DiscordCommandManager;
import github.vaporrrr.btediscordaddon.schematics.Schematics;
import org.bukkit.configuration.file.FileConfiguration;

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
        FileConfiguration config = bteDiscordAddon.getConfig();
        if (config.getBoolean("Stats.Minecraft.Enabled")) {
            bteDiscordAddon.restartStats();
        }
        if (config.getBoolean("Stats.Team.Enabled")) {
            bteDiscordAddon.restartStats();
        }
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
        if (event.getMessage().getContentRaw().length() < 2) return;
        if (!event.getMessage().getContentRaw().substring(0, 1).equals(bteDiscordAddon.getConfig().getString("DiscordCommandsPrefix"))) {
            return;
        }
        String[] args = event.getMessage().getContentRaw().split(" ");
        String command = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);
        discordCommandManager.executeCommand(event, command, args);
    }

    @Subscribe
    public void accountsLinked(AccountLinkedEvent event) {
        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("links");

        // null if the channel isn't specified in the config.yml
        if (textChannel != null) {
            textChannel.sendMessage(event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId() + ") has linked their associated Discord account: "
                    + (event.getUser() != null ? event.getUser().getName() : "<not available>") + " (" + (event.getUser() != null ? event.getUser().getId() : "<not available>") + ")").queue();
        } else {
            bteDiscordAddon.getLogger().warning("Channel called \"links\" could not be found in the DiscordSRV configuration");
        }
    }

    @Subscribe
    public void accountUnlinked(AccountUnlinkedEvent event) {
        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("unlinks");

        // null if the channel isn't specified in the config.yml
        if (textChannel != null) {
            textChannel.sendMessage(event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId() + ") has unlinked their associated Discord account: "
                    + (event.getDiscordUser() != null ? event.getDiscordUser().getName() : "<not available>") + " (" + event.getDiscordId() + ")").queue();
        } else {
            bteDiscordAddon.getLogger().warning("Channel called \"unlinks\" could not be found in the DiscordSRV configuration");
        }
    }
}
