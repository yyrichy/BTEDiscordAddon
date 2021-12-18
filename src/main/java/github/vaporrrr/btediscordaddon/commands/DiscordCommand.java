package github.vaporrrr.btediscordaddon.commands;

import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class DiscordCommand {
    protected boolean hasPermission(BTEDiscordAddon bteDiscordAddon, Member member) {
        FileConfiguration config = bteDiscordAddon.getConfig();
        if (!config.getBoolean("DiscordCommands." + getName() + ".Enabled")) return false;
        return config.getStringList("DiscordCommands." + getName() + ".Permissions.Roles").stream().anyMatch(id -> member.getRoles().stream().anyMatch(r -> r.getId().equals(id)));
    }

    public abstract void execute(BTEDiscordAddon bteDiscordAddon, DiscordGuildMessageReceivedEvent event, String[] args);

    public abstract String getName();

    public abstract String[] getArguments();
}