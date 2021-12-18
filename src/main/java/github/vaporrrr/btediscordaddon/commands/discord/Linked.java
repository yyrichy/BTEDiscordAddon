package github.vaporrrr.btediscordaddon.commands.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import github.vaporrrr.btediscordaddon.commands.DiscordCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Linked extends DiscordCommand {
    @Override
    public void execute(BTEDiscordAddon bteDiscordAddon, DiscordGuildMessageReceivedEvent event, String[] args) {
        /*
            Adapted from DiscordSRV's /discord linked
         */
        try {
            if(args.length < 1){
                event.getChannel().sendMessage("Specify a UUID, Discord ID, Minecraft player name, or Discord name.").queue();
            } else {
                String target = args[0];
                String joinedTarget = String.join(" ", args);

                if (args.length == 1 && target.length() == 32 || target.length() == 36) {
                    // target is UUID
                    notifyInterpret(event.getChannel(), "UUID");
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(target));
                    notifyPlayer(event.getChannel(), player);
                    notifyDiscord(event.getChannel(), DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId()));
                    return;
                } else if (args.length == 1 && DiscordUtil.getUserById(target) != null ||
                        (StringUtils.isNumeric(target) && target.length() >= 17 && target.length() <= 20)) {
                    // target is a Discord ID
                    notifyInterpret(event.getChannel(), "Discord ID");
                    UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(target);
                    notifyPlayer(event.getChannel(), uuid != null ? Bukkit.getOfflinePlayer(uuid) : null);
                    notifyDiscord(event.getChannel(), target);
                    return;
                } else {
                    if (args.length == 1 && target.length() >= 3 && target.length() <= 16) {
                        // target is probably a Minecraft player name
                        OfflinePlayer player;

                        player = Bukkit.getOnlinePlayers().stream()
                                .filter(p -> p.getName().equalsIgnoreCase(target))
                                .findFirst().orElse(null);

                        if (player == null) {
                            player = Arrays.stream(Bukkit.getOfflinePlayers())
                                    .filter(p -> p.getName() != null && p.getName().equalsIgnoreCase(target))
                                    .findFirst().orElse(null);
                        }

                        if (player == null) {
                            //noinspection deprecation
                            player = Bukkit.getOfflinePlayer(target);
                            if (player.getName() == null) {
                                // player doesn't actually exist
                                player = null;
                            }
                        }

                        if (player != null) {
                            // found them
                            notifyInterpret(event.getChannel(), "Minecraft player");
                            notifyPlayer(event.getChannel(), player);
                            notifyDiscord(event.getChannel(), DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId()));
                            return;
                        }
                    }

                    if (joinedTarget.contains("#") || (joinedTarget.length() >= 2 && joinedTarget.length() <= 32 + 5)) {
                        // target is a discord name... probably.
                        String targetUsername = joinedTarget.contains("#") ? joinedTarget.split("#")[0] : joinedTarget;
                        String discriminator = joinedTarget.contains("#") ? joinedTarget.split("#")[1] : "";

                        Set<User> matches = DiscordUtil.getJda().getGuilds().stream()
                                .flatMap(guild -> guild.getMembers().stream())
                                .filter(member -> member.getUser().getName().equalsIgnoreCase(targetUsername)
                                        || (member.getNickname() != null && member.getNickname().equalsIgnoreCase(targetUsername)))
                                .map(Member::getUser)
                                .filter(user -> user.getDiscriminator().contains(discriminator))
                                .collect(Collectors.toSet());

                        if (matches.size() >= 1) {
                            notifyInterpret(event.getChannel(), "Discord name");

                            matches.stream().limit(5).forEach(user -> {
                                UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(user.getId());
                                notifyPlayer(event.getChannel(), uuid != null ? Bukkit.getOfflinePlayer(uuid) : null);
                                notifyDiscord(event.getChannel(), user.getId());
                            });

                            int remaining = matches.size() - 5;
                            if (remaining >= 1) {
                                event.getChannel().sendMessage("More results... " + remaining).queue();
                            }
                            return;
                        }
                    }
                }
                // no matches at all found
                event.getChannel().sendMessage("Could not find matches for user/player.").queue();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void notifyInterpret(TextChannel channel, String type) {
        channel.sendMessage("Interpreted as: " + type).queue();
    }

    private static void notifyPlayer(TextChannel channel, OfflinePlayer player) {
        if (player != null) {
            channel.sendMessage("Player: " + player.getName() + " " + player.getUniqueId()).queue();
        } else {
            channel.sendMessage("Player: null").queue();
        }
    }

    private static void notifyDiscord(TextChannel channel, String discordId) {
        User user = DiscordUtil.getUserById(discordId);
        String discordInfo = (user != null ? " (" + user.getName() + "#" + user.getDiscriminator() + ")" : "") + " " + discordId;
        channel.sendMessage("Discord:" + discordInfo).queue();
    }

    @Override
    public String getName() {
        return "linked";
    }

    @Override
    public String[] getArguments() {
        return new String[]{ "{input(UUID, Minecraft Name, Discord ID, Discord Name)}" };
    }
}