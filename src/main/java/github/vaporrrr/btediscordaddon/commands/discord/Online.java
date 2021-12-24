package github.vaporrrr.btediscordaddon.commands.discord;

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import github.vaporrrr.btediscordaddon.commands.DiscordCommand;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.ArrayList;

public class Online extends DiscordCommand {
    @Override
    public void execute(BTEDiscordAddon bteDiscordAddon, DiscordGuildMessageReceivedEvent event, String[] args) {
        ArrayList<String> playerList = bteDiscordAddon.getUserManager().playerList();
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.WHITE);
        embed.setTitle(bteDiscordAddon.getConfig().getString("DiscordCommands." + getName() + ".Title"));
        if (playerList.size() == 0) {
            embed.setDescription("No Players Online");
        } else {
            embed.addField(playerList.size() + "/" + Bukkit.getMaxPlayers() + " Player(s) Online", String.join("\n", playerList), false);
        }
        event.getChannel().sendMessage(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "online";
    }

    @Override
    public String[] getArguments() {
        return null;
    }
}
