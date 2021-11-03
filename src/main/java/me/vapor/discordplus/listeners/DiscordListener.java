package me.vapor.discordplus.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import me.vapor.discordplus.MinecraftStats;
import me.vapor.discordplus.Status;
import me.vapor.discordplus.TeamStats;
import me.vapor.discordplus.commands.discord.Linked;
import me.vapor.discordplus.commands.discord.Setup;
import me.vapor.discordplus.schematics.Download;
import me.vapor.discordplus.schematics.Upload;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class DiscordListener {
    private final Plugin plugin;
    private final Upload upload;
    private final Download download;


    public DiscordListener(Plugin plugin){
        this.plugin = plugin;
        this.upload = new Upload(plugin);
        this.download = new Download(plugin);
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new JDAListener(plugin));
        plugin.getLogger().info("Discord Ready!");
        Status.updateEmbed(null, false, null);
        Timer t = new Timer();
        int mcInterval = plugin.getConfig().getInt("MinecraftStatsEditIntervalInSeconds");
        int teamInterval = plugin.getConfig().getInt("TeamStatsEditIntervalInSeconds");
        MinecraftStats mTask = new MinecraftStats(plugin, mcInterval);
        TeamStats tTask = new TeamStats(plugin, teamInterval);
        t.scheduleAtFixedRate(mTask, 0, mcInterval * 1000L);
        t.scheduleAtFixedRate(tTask, 0, teamInterval * 1000L);
    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getId().equals(plugin.getConfig().getString("UploadSchematicsChannelID"))) {upload.execute(event); return;}
        if (event.getChannel().getId().equals(plugin.getConfig().getString("DownloadSchematicsChannelID"))) {download.execute(event); return;}
        if (event.getChannel().getId().equals(plugin.getConfig().getString("LinkAccountChannelID"))) {
            try {
                event.getChannel().sendMessage(DiscordSRV.getPlugin().getAccountLinkManager().process(event.getMessage().getContentRaw(), event.getAuthor().getId())).complete().delete().completeAfter(plugin.getConfig().getInt("DelayBeforeDeleteLinkAccountMessageInSeconds"), TimeUnit.SECONDS);
                event.getMessage().delete().queue();
            } catch (Exception e){
                e.printStackTrace();
            }
            return;
        }
        if (event.getMessage().getContentRaw().length() < 2) return;
        if (!event.getMessage().getContentRaw().substring(0, 1).equals(plugin.getConfig().getString("DiscordPrefix"))) return;
        String[] args = event.getMessage().getContentRaw().split(" ");
        String command = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);

        if (command.equals("setup")) {
            Setup.execute(event, args, plugin);
        } else if (command.equals("linked")) {
            Linked.execute(event, args, plugin);
        }
    }

    @Subscribe
    public void accountsLinked(AccountLinkedEvent event) {
        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("links");

        // null if the channel isn't specified in the config.yml
        if (textChannel != null) {
            textChannel.sendMessage(event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId() + ") has linked their associated Discord account: "
                    + (event.getUser() != null ? event.getUser().getName() : "<not available>") + " (" + (event.getUser() != null ? event.getUser().getId() : "<not available>") + ")").queue();
        } else {
            plugin.getLogger().warning("Channel called \"links\" could not be found in the DiscordSRV configuration");
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
            plugin.getLogger().warning("Channel called \"unlinks\" could not be found in the DiscordSRV configuration");
        }
    }
}
