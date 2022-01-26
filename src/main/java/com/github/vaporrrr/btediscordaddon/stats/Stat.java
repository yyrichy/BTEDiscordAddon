package com.github.vaporrrr.btediscordaddon.stats;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import com.github.vaporrrr.btediscordaddon.util.MessageUtil;
import com.github.vaporrrr.btediscordaddon.util.Placeholder;
import de.leonhard.storage.Config;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.util.PlaceholderUtil;

import java.util.concurrent.TimeUnit;

public class Stat extends Thread {
    private final EmbedBuilder embed = new EmbedBuilder();
    private final String name;

    public Stat(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        try {
            while (!interrupted()) {
                Config config = BTEDiscordAddon.config();
                long interval = config.getLong("Stats." + name + ".IntervalInSeconds");
                if (interval < 20) {
                    BTEDiscordAddon.warn("Stats." + name + ".IntervalInSeconds is set to below 20 seconds, overriding to a 20 second interval.");
                    BTEDiscordAddon.warn("You should not be using such a low interval, ESPECIALLY if you are using the website API placeholders.");
                    config.set("Stats." + name + ".IntervalInSeconds", 20);
                    interval = 20;
                }
                embed.clear();
                embed.setTitle(name + " Statistics");
                for (String value : config.getStringList("Stats." + name + ".Description")) {
                    add(format(value));
                }
                embed.setFooter("Message updated every " + interval + " seconds");
                MessageUtil.editMessageFromConfig("Stats." + name + ".ChannelID", "Stats." + name + ".MessageID", embed.build(), name + " Statistics");
                Thread.sleep(TimeUnit.SECONDS.toMillis(interval));
            }
        } catch (InterruptedException ignored) {
        }
    }

    private void add(String value) {
        embed.appendDescription("\n" + value);
    }

    private String format(String value) {
        value = Placeholder.replacePlaceholdersToDiscord(value);
        value = PlaceholderUtil.replacePlaceholdersToDiscord(value);
        return value;
    }
}
