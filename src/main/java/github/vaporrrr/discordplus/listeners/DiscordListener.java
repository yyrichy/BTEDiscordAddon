package github.vaporrrr.discordplus.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.vaporrrr.discordplus.DiscordPlus;
import github.vaporrrr.discordplus.MinecraftStats;
import github.vaporrrr.discordplus.TeamStats;
import github.vaporrrr.discordplus.commands.discord.Linked;
import github.vaporrrr.discordplus.commands.discord.Setup;
import github.vaporrrr.discordplus.schematics.Download;
import github.vaporrrr.discordplus.schematics.Upload;

import java.util.Arrays;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class DiscordListener {
    private final DiscordPlus discordPlus;
    private final Upload upload;
    private final Download download;


    public DiscordListener(DiscordPlus discordPlus){
        this.discordPlus = discordPlus;
        this.upload = new Upload(this.discordPlus);
        this.download = new Download(this.discordPlus);
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        discordPlus.getLogger().info("Discord Ready!");
        discordPlus.getServerStatus().update();
        if (discordPlus.getConfig().getBoolean("StatsEnabled")) {
            Timer t = new Timer();
            int mcInterval = discordPlus.getConfig().getInt("MinecraftStatsEditIntervalInSeconds");
            int teamInterval = discordPlus.getConfig().getInt("TeamStatsEditIntervalInSeconds");
            MinecraftStats mTask = new MinecraftStats(discordPlus, mcInterval);
            TeamStats tTask = new TeamStats(discordPlus, teamInterval);
            t.scheduleAtFixedRate(mTask, 0, mcInterval * 1000L);
            t.scheduleAtFixedRate(tTask, 0, teamInterval * 1000L);
        }
    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getChannel().getId().equals(discordPlus.getConfig().getString("UploadSchematicsChannelID"))) {upload.execute(event); return;}
        if (event.getChannel().getId().equals(discordPlus.getConfig().getString("DownloadSchematicsChannelID"))) {download.execute(event); return;}
        if (event.getChannel().getId().equals(discordPlus.getConfig().getString("LinkAccountChannelID"))) {
            try {
                event.getChannel().sendMessage(DiscordSRV.getPlugin().getAccountLinkManager().process(event.getMessage().getContentRaw(), event.getAuthor().getId())).complete().delete().completeAfter(discordPlus.getConfig().getInt("DelayBeforeDeleteLinkAccountMessageInSeconds"), TimeUnit.SECONDS);
                event.getMessage().delete().queue();
            } catch (Exception e){
                e.printStackTrace();
            }
            return;
        }
        if (event.getMessage().getContentRaw().length() < 2) return;
        if (!event.getMessage().getContentRaw().substring(0, 1).equals(discordPlus.getConfig().getString("DiscordPrefix"))) return;
        String[] args = event.getMessage().getContentRaw().split(" ");
        String command = args[0].substring(1);
        args = Arrays.copyOfRange(args, 1, args.length);

        if (command.equals("setup")) {
            Setup.execute(event, args, discordPlus);
        } else if (command.equals("linked")) {
            Linked.execute(event, args, discordPlus);
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
            discordPlus.getLogger().warning("Channel called \"links\" could not be found in the DiscordSRV configuration");
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
            discordPlus.getLogger().warning("Channel called \"unlinks\" could not be found in the DiscordSRV configuration");
        }
    }
}
