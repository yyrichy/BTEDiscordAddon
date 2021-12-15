package github.vaporrrr.btediscordaddon;

import org.bukkit.entity.Player;

public class User {
    private final Player player;
    private boolean isAfk;
    public User (Player player, boolean isAfk) {
        this.player = player;
        this.isAfk = isAfk;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isAfk() {
        return isAfk;
    }

    public void setAfk(boolean afk) {
        isAfk = afk;
    }
}
