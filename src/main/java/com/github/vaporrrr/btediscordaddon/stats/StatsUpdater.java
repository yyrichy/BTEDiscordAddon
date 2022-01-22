package com.github.vaporrrr.btediscordaddon.stats;

import com.github.vaporrrr.btediscordaddon.BTEDiscordAddon;
import de.leonhard.storage.Config;

import java.util.ArrayList;
import java.util.Set;

public class StatsUpdater extends Thread {
    private final ArrayList<Stat> statsList = new ArrayList<>();

    @Override
    public void run() {
        reload();
    }

    public void reload() {
        for (Stat stat : statsList) {
            if (!stat.isInterrupted()) stat.interrupt();
        }
        statsList.clear();
        Config config = BTEDiscordAddon.config();
        Set<String> stats = config.singleLayerKeySet("Stats");
        for (String name : stats) {
            if (config.getBoolean("Stats." + name + ".Enabled")) statsList.add(new Stat(name));
        }
        for (Stat stat : statsList) {
            stat.start();
        }
    }
}