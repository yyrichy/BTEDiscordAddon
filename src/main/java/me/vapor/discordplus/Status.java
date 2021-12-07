package me.vapor.discordplus;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Status {
    private static final JDA jda = DiscordUtil.getJda();
    private static final Plugin plugin = DiscordPlus.getPlugin(DiscordPlus.class);
    private static final DiscordSRV discordSRV = DiscordSRV.getPlugin();
    static Essentials ess = (Essentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

    public static void updateEmbed(PlayerQuitEvent quitEvent, Boolean offline, AfkStatusChangeEvent afkEvent) {
        String ChannelID = plugin.getConfig().getString("ChannelID");
        String MessageID = plugin.getConfig().getString("MessageID");
        EmbedBuilder statusEmbed = new EmbedBuilder();
        statusEmbed.setAuthor((plugin.getConfig().getString("ServerIP").isEmpty() ? "Minecraft Server Status" : plugin.getConfig().getString("ServerIP")), null, "https://cdn.discordapp.com/attachments/809938188155486239/879114388081086475/terraextruded.gif");
        if(offline){
            statusEmbed.setColor(Color.decode("#962d2d"));
            statusEmbed.addField("Server Offline", plugin.getConfig().getString("OfflineMessage"), false);
            Objects.requireNonNull(jda.getTextChannelById(ChannelID)).editMessageById(MessageID, statusEmbed.build()).queue();
            return;
        }
        int maxPlayerCount = Bukkit.getMaxPlayers();
        ArrayList<String> onlinePlayersList;
        int playerCount;

        if(quitEvent != null){
            onlinePlayersList = playerList(quitEvent);
            playerCount = Bukkit.getOnlinePlayers().size() - 1;
        } else {
            if(afkEvent != null && !afkEvent.isCancelled()){
                onlinePlayersList = playerList(afkEvent);
            } else {
                onlinePlayersList = playerList();
            }
            playerCount = Bukkit.getOnlinePlayers().size();
        }

        plugin.getLogger().info("Updating Embed | " + playerCount +" | " + String.join(", ", onlinePlayersList));
        if(playerCount < 1 || onlinePlayersList.isEmpty()) {
            statusEmbed.setColor(Color.decode("#" + plugin.getConfig().getString("NoPlayersHexColor"))); //hex code for orange
            statusEmbed.addField("No Players Online", "```):```", false);
        } else {
            statusEmbed.setColor(Color.decode("#a1ee33")); //hex code for green
            statusEmbed.addField(playerCount + "/" + maxPlayerCount + " Player(s) Online", String.join("\n", onlinePlayersList), false);
        }
        Objects.requireNonNull(jda.getTextChannelById(ChannelID)).editMessageById(MessageID, statusEmbed.build()).queue();
    }

    public static ArrayList<String> playerList(){
        ArrayList<String> playerList = new ArrayList<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            User user = ess.getUser(player);
            playerList.add((user.isAfk() ? "[AFK]" : "") + player.getName() + getDiscordMentionFromUUID(player.getUniqueId()));
        }
        playerList.sort(String.CASE_INSENSITIVE_ORDER);
        return playerList;
    }

    public static ArrayList<String> playerList(AfkStatusChangeEvent event){
        ArrayList<String> playerList = new ArrayList<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (event.getAffected().getName().equals(player.getName()))
                playerList.add((event.getValue() ? "[AFK]" : "") + player.getName() + getDiscordMentionFromUUID(player.getUniqueId()));
            else {
                User user = ess.getUser(player);
                playerList.add((user.isAfk() ? "[AFK]" : "") + player.getName() + getDiscordMentionFromUUID(player.getUniqueId()));
            }
        }
        playerList.sort(String.CASE_INSENSITIVE_ORDER);
        return playerList;
    }

    public static ArrayList<String> playerList(PlayerQuitEvent event){
        ArrayList<String> playerList = new ArrayList<>();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!event.getPlayer().getName().equals(player.getName())) {
                User user = ess.getUser(player);
                playerList.add((user.isAfk() ? "[AFK]" : "") + player.getName() + getDiscordMentionFromUUID(player.getUniqueId()));
            }
        }
        playerList.sort(String.CASE_INSENSITIVE_ORDER);
        return playerList;
    }

    public static String getDiscordMentionFromUUID(UUID UUID) {
        String discordId = discordSRV.getAccountLinkManager().getDiscordId(UUID);
        if (discordId != null) {
            return " <@" + discordId + ">";
        }
        return "";
    }
}