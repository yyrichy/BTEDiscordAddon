package github.vaporrrr.discordplus;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

public class ServerStatus {
    private final DiscordPlus discordPlus;
    private final DiscordSRV discordSRV = DiscordSRV.getPlugin();
    public ServerStatus(DiscordPlus discordPlus) {
        this.discordPlus = discordPlus;
    }

    public void update() {
        JDA jda = discordSRV.getJda();
        Logger logger = discordPlus.getLogger();
        FileConfiguration config = discordPlus.getConfig();
        TextChannel channel = jda.getTextChannelById(config.getString("ServerStatus.ChannelID"));
        if (channel == null) {
            logger.severe("Could not find TextChannel from ServerStatus.ChannelID");
            return;
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(config.getString("ServerStatus.Title"));
        if (Bukkit.getOnlinePlayers().size() == 0) {
            embed.setDescription("No Players Online.");
        } else {
            embed.addField(Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + " Player(s) Online", String.join("\n", playerList()), false);
        }
        channel.editMessageById(config.getString("ServerStatus.MessageID"), embed.build()).queue();
    }

    private ArrayList<String> playerList() {
        ArrayList<String> playerList = new ArrayList<>();
        HashMap<UUID, User> userMap = discordPlus.getUserManager().getUserMap();
        for (User user : userMap.values()) {
            playerList.add(format(user));
        }
        return playerList;
    }

    private String format(User user) {
        String format = discordPlus.getConfig().getString("ServerStatus.NameFormat");
        UUID UUID = user.getPlayer().getUniqueId();
        format = format.replace("%username%", getFormattedMinecraftUsername(user));
        if (getDiscordIDFromUUID(UUID) != null) {
            format = format.replace("%discord-mention%", getDiscordMentionFromUUID(UUID));
            format = format.replace("%discord-tag%", getDiscordTagFromUUID(UUID));
            format = format.replace("%discord-username%", getDiscordUsernameFromUUID(UUID));
            format = format.replace("%discord-id%", getDiscordIDFromUUID(UUID));
        }
        return format;
    }

    private String getDiscordTagFromUUID(UUID UUID) {
        github.scarsz.discordsrv.dependencies.jda.api.entities.User user = getDiscordUserFromUUID(UUID);
        if (user == null) return "";
        return user.getAsTag();
    }

    private String getDiscordUsernameFromUUID(UUID UUID) {
        github.scarsz.discordsrv.dependencies.jda.api.entities.User user = getDiscordUserFromUUID(UUID);
        if (user == null) return "";
        return user.getName();
    }

    private github.scarsz.discordsrv.dependencies.jda.api.entities.User getDiscordUserFromUUID(UUID UUID) {
        return discordSRV.getJda().getUserById(getDiscordIDFromUUID(UUID));
    }

    private String getFormattedMinecraftUsername(User user) {
        String name = user.getPlayer().getName().replace("_", "\\_") + getDiscordMentionFromUUID(user.getPlayer().getUniqueId());
        return user.isAfk() ? "[AFK]" + name : name;
    }

    private String getDiscordMentionFromUUID(UUID UUID) {
        return getDiscordIDFromUUID(UUID) == null ? "" : "<@!" + getDiscordIDFromUUID(UUID) + ">";
    }

    private String getDiscordIDFromUUID(UUID UUID) {
        return discordSRV.getAccountLinkManager().getDiscordId(UUID);
    }
}
