package com.github.vaporrrr.btediscordaddon.util;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;

public class MessageUtil {
    public static <T> void editMessage(String channelID, String messageID, T content, String procedure) {
        TextChannel channel = DiscordUtil.getJda().getTextChannelById(channelID);
        if (channel == null) {
            BTEDiscordAddon.severe(procedureFormat(procedure, "Could not get TextChannel from " + channelID));
        } else if (content instanceof MessageEmbed){
            channel.retrieveMessageById(messageID).queue((message) -> message.editMessage((MessageEmbed) content).queue(), (failure) -> BTEDiscordAddon.severe(procedureFormat(procedure, "Could not edit message " + messageID + " in #" + channel.getName())));
        } else if (content instanceof String) {
            channel.retrieveMessageById(messageID).queue((message) -> message.editMessage((String) content).queue(), (failure) -> BTEDiscordAddon.severe(procedureFormat(procedure,"Could not edit message " + messageID + " in #" + channel.getName())));
        }
    }

    public static <T> void editMessageFromConfig(String channelKey, String messageKey, T content, String procedure) {
        String channelID = BTEDiscordAddon.config().getString(channelKey);
        String messageID = BTEDiscordAddon.config().getString(messageKey);
        if (channelID == null) {
            BTEDiscordAddon.warn(procedureFormat(procedure, channelKey + " is not defined in the config."));
        } else if (messageID == null) {
            BTEDiscordAddon.warn(procedureFormat(procedure, messageKey + " is not defined in the config."));
        } else {
            editMessage(channelID, messageID, content, procedure);
        }
    }

    private static String procedureFormat(String procedure, String content) {
        return "(" + procedure + ") - " + content;
    }

    public static String escapeMarkdown(String text) {
        return text.replace("_", "\\_").replace("*", "\\*").replace("`", "\\`").replace("~", "\\~").replace("|", "\\|").replace(">", "\\>");
    }
}
