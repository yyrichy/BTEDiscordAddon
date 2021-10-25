package me.vapor.discordplus.commands.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Linked {
    public static void execute(DiscordGuildMessageReceivedEvent e, String[] args, Plugin plugin) {
        if (!e.getMember().hasPermission(Permission.BAN_MEMBERS)) return;
        if (!e.getGuild().getId().equals(plugin.getConfig().getString("LinkedCommandGuildID"))) return;
        try {
            if(args.length < 1){
                e.getChannel().sendMessage("Specify a UUID, Discord ID, Minecraft player name, or Discord name.").queue();
            } else {
                String target = args[0];
                String joinedTarget = String.join(" ", args);

                if (args.length == 1 && target.length() == 32 || target.length() == 36) {
                    // target is UUID
                    notifyInterpret(e.getChannel(), "UUID");
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(target));
                    notifyPlayer(e.getChannel(), player);
                    notifyDiscord(e.getChannel(), DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId()));
                    return;
                } else if (args.length == 1 && DiscordUtil.getUserById(target) != null ||
                        (StringUtils.isNumeric(target) && target.length() >= 17 && target.length() <= 20)) {
                    // target is a Discord ID
                    notifyInterpret(e.getChannel(), "Discord ID");
                    UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(target);
                    notifyPlayer(e.getChannel(), uuid != null ? Bukkit.getOfflinePlayer(uuid) : null);
                    notifyDiscord(e.getChannel(), target);
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
                            notifyInterpret(e.getChannel(), "Minecraft player");
                            notifyPlayer(e.getChannel(), player);
                            notifyDiscord(e.getChannel(), DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId()));
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
                                .filter(member -> member.getUser().getDiscriminator().contains(discriminator))
                                .map(Member::getUser)
                                .collect(Collectors.toSet());

                        if (matches.size() >= 1) {
                            notifyInterpret(e.getChannel(), "Discord name");

                            matches.stream().limit(5).forEach(user -> {
                                UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(user.getId());
                                notifyPlayer(e.getChannel(), uuid != null ? Bukkit.getOfflinePlayer(uuid) : null);
                                notifyDiscord(e.getChannel(), user.getId());
                            });

                            int remaining = matches.size() - 5;
                            if (remaining >= 1) {
                                e.getChannel().sendMessage("More results... " + remaining).queue();
                            }
                            return;
                        }
                    }
                }

                // no matches at all found
                e.getChannel().sendMessage("Could not find matches for user/player.").queue();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static void notifyInterpret(TextChannel channel, String type) {
        channel.sendMessage("Interpreted as: " + type).queue();
    }

    static void notifyPlayer(TextChannel channel, OfflinePlayer player) {
        if(player != null){
            channel.sendMessage("Player: " + player.getName() + " " + player.getUniqueId()).queue();
        } else {
            channel.sendMessage("Player: null").queue();
        }

    }

    static void notifyDiscord(TextChannel channel, String discordId) {
        User user = DiscordUtil.getUserById(discordId);
        String discordInfo = (user != null ? " (" + user.getName() + "#" + user.getDiscriminator() + ")" : "") + " " + discordId;
        channel.sendMessage("Discord:" + discordInfo).queue();
    }
}
