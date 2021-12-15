package github.vaporrrr.discordplus;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class UserManager {
    private final HashMap<UUID, User> userMap = new HashMap<>();

    public UserManager() {

    }

    public void add(Player player) {
        userMap.put(player.getUniqueId(), new User(player, false));
    }

    public void remove(Player player) {
        userMap.remove(player.getUniqueId());
    }

    public void updateAfk(Player player) {
        if (userMap.containsKey(player.getUniqueId())) {
            User user = userMap.get(player.getUniqueId());
            user.setAfk(!user.isAfk());
        }
    }

    public HashMap<UUID, User> getUserMap() {
        return userMap;
    }
}
