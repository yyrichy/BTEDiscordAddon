package github.vaporrrr.btediscordaddon.commands.discord;

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import github.vaporrrr.btediscordaddon.commands.DiscordCommand;

public class Setup extends DiscordCommand {
    @Override
    public void execute(BTEDiscordAddon bteDiscordAddon, DiscordGuildMessageReceivedEvent event, String[] args) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription("Minecraft Server Status will be edited into this message. Type " + bteDiscordAddon.getConfig().getString("DiscordCommandsPrefix") + getName() + " again to change the location.");
        embed.setFooter("Editing config...");
        event.getChannel().sendMessage(embed.build()).queue((m -> {
            bteDiscordAddon.getConfig().set("ServerStatus.ChannelID", m.getTextChannel().getId());
            bteDiscordAddon.getConfig().set("ServerStatus.MessageID", m.getId());
            bteDiscordAddon.saveConfig();
            embed.setFooter("Success editing config!");
            m.editMessage(embed.build()).queue();
        }));
    }

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public String[] getArguments() {
        return null;
    }
}
