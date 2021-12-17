package github.vaporrrr.btediscordaddon;

import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

public class User {
    private final Player player;
    private boolean isAfk;
    private int interval;
    private final Timer t = new Timer();

    public User(Player player, boolean isAfk, int interval) {
        this.player = player;
        this.isAfk = isAfk;
        this.interval = interval;
        startAfkTimer();
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isAfk() {
        return isAfk;
    }

    public void setAfk(boolean afk) {
        isAfk = afk;
        System.out.println("method set afk to " + afk);
        //Remove later when chat event
        if (afk) startAfkTimer();
    }

    public void startAfkTimer() {
        if (!(interval > 0)) return;
        t.cancel();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                isAfk = true;
                System.out.println("Set afk to true for " + player.getName());
                t.cancel();
            }
        }, interval * 1000L);
    }
}
