package com.github.vaporrrr.btediscordaddon.commands.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.commands.DiscordCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Linked extends DiscordCommand {
    private final EmbedBuilder embed = new EmbedBuilder();
    private TextChannel channel;

    @Override
    public void execute(BTEDiscordAddon bteDiscordAddon, DiscordGuildMessageReceivedEvent event, String[] args) {
        /*
            Adapted from DiscordSRV's /discord linked
        */
        embed.clear();
        embed.setTitle(getName().toUpperCase());
        channel = event.getChannel();
        try {
            if (args.length < 1) {
                channel.sendMessage("Specify a UUID, Discord ID, Minecraft player name, or Discord name.").queue();
            } else {
                String target = args[0];
                String joinedTarget = String.join(" ", args);
                if (args.length == 1 && target.length() == 32 || target.length() == 36) {
                    //Target is UUID
                    notifyInterpret("UUID");
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(target));
                    notifyPlayer(player);
                    notifyDiscord(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId()));
                    sendEmbed();
                    return;
                } else if (args.length == 1 && DiscordUtil.getUserById(target) != null ||
                        (StringUtils.isNumeric(target) && target.length() >= 17 && target.length() <= 20)) {
                    //Target is Discord ID
                    notifyInterpret("Discord ID");
                    UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(target);
                    notifyPlayer(uuid != null ? Bukkit.getOfflinePlayer(uuid) : null);
                    notifyDiscord(target);
                    sendEmbed();
                    return;
                } else {
                    if (args.length == 1 && target.length() >= 3 && target.length() <= 16) {
                        //Target probably a Minecraft player name
                        OfflinePlayer player = Bukkit.getOnlinePlayers().stream()
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
                                //Player doesn't actually exist
                                player = null;
                            }
                        }
                        if (player != null) {
                            //Found
                            notifyInterpret("Minecraft player");
                            notifyPlayer(player);
                            notifyDiscord(DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId()));
                            sendEmbed();
                            return;
                        }
                    }
                    if (joinedTarget.contains("#") || (joinedTarget.length() >= 2 && joinedTarget.length() <= 32 + 5)) {
                        //Target is probably a Discord name
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
                            notifyInterpret("Discord name");
                            matches.stream().limit(5).forEach(user -> {
                                UUID uuid = DiscordSRV.getPlugin().getAccountLinkManager().getUuid(user.getId());
                                notifyPlayer(uuid != null ? Bukkit.getOfflinePlayer(uuid) : null);
                                notifyDiscord(user.getId());
                            });
                            sendEmbed();
                            int remaining = matches.size() - 5;
                            if (remaining >= 1) {
                                channel.sendMessage("More results... " + remaining).queue();
                            }
                            return;
                        }
                    }
                }
                //No matches found
                channel.sendMessage("Could not find matches for user/player.").queue();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void notifyInterpret(String type) {
        embed.appendDescription("\n`Interpreted as:` " + type);
    }

    private void notifyPlayer(OfflinePlayer player) {
        if (player != null) {
            embed.appendDescription("\n`Player:` " + player.getName() + " " + player.getUniqueId());
        } else {
            embed.appendDescription("\n`Player:` Not Found");
        }
    }

    private void notifyDiscord(String discordId) {
        User user = DiscordUtil.getUserById(discordId);
        String discordInfo = (user != null ? " (" + user.getName() + "#" + user.getDiscriminator() + ")" : "") + " " + discordId;
        embed.appendDescription("\n`Discord:`" + discordInfo);
    }

    private void sendEmbed() {
        channel.sendMessage(embed.build()).queue();
    }

    @Override
    public String getName() {
        return "linked";
    }

    @Override
    public String[] getArguments() {
        return new String[]{"{input(UUID, Minecraft Name, Discord ID, Discord Name)}"};
    }
}