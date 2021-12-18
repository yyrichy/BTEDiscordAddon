package github.vaporrrr.btediscordaddon.commands;

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import github.vaporrrr.btediscordaddon.commands.discord.Linked;
import github.vaporrrr.btediscordaddon.commands.discord.Setup;

import java.util.HashMap;

public class DiscordCommandManager {
    private final BTEDiscordAddon bteDiscordAddon;
    private final HashMap<String, DiscordCommand> discordCommands = new HashMap<>();
    public DiscordCommandManager(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
        Linked linked = new Linked();
        discordCommands.put(linked.getName(), linked);
        Setup setup = new Setup();
        discordCommands.put(setup.getName(), setup);
    }

    public void executeCommand(DiscordGuildMessageReceivedEvent event, String command, String[] args) {
        DiscordCommand discordCommand = discordCommands.get(command);
        if (discordCommand != null) {
            if (discordCommand.hasPermission(bteDiscordAddon, event.getMember())) {
                if (discordCommand.getArguments() != null && args.length < discordCommand.getArguments().length) {
                    event.getChannel().sendMessage("Usage: " + bteDiscordAddon.getConfig().getString("DiscordCommandsPrefix") + discordCommand.getName() + " " + String.join(" ", discordCommand.getArguments())).queue();
                    return;
                }
                discordCommand.execute(bteDiscordAddon, event, args);
            }
        }
    }
}
