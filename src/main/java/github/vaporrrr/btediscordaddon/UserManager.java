package github.vaporrrr.btediscordaddon;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    private final HashMap<UUID, User> userMap = new HashMap<>();
    private final BTEDiscordAddon bteDiscordAddon;

    public UserManager(BTEDiscordAddon bteDiscordAddon) {
        this.bteDiscordAddon = bteDiscordAddon;
    }

    public void add(Player player) {
        userMap.put(player.getUniqueId(), new User(player, false, bteDiscordAddon.getConfig().getInt("AfkAutoInSeconds")));
    }

    public void remove(Player player) {
        userMap.remove(player.getUniqueId());
    }

    public User getUser(Player player) {
        return userMap.get(player.getUniqueId());
    }

    public void toggleAfk(Player player) {
        setAfk(player, userMap.get(player.getUniqueId()).isAfk());
    }

    public void setAfk(Player player, boolean afk) {
        userMap.get(player.getUniqueId()).setAfk(afk);
    }

    public HashMap<UUID, User> getUserMap() {
        return userMap;
    }
}
