package github.vaporrrr.discordplus.commands.discord;

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import org.bukkit.plugin.Plugin;

public class Setup {
    public static void execute(DiscordGuildMessageReceivedEvent e, String[] args, Plugin plugin) {
        if (!e.getMember().hasPermission(Permission.BAN_MEMBERS)) return;
        try {
            EmbedBuilder Embed = new EmbedBuilder();
            Embed.setDescription("Minecraft Server Status will be edited into this message. Use the command again to change the location of the message.");
            e.getChannel().sendMessage(Embed.build()).queue((m -> {
                plugin.getConfig().set("ChannelID", m.getTextChannel().getId());
                plugin.getConfig().set("MessageID", m.getId());
                plugin.saveConfig();
            }));
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
