package github.vaporrrr.btediscordaddon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Timer;
import java.util.TimerTask;

public class User {
    private final Player player;
    private boolean isAfk;
    private final Timer t = new Timer();
    private TimerTask afkTimerTask = new TimerTask() {
        @Override
        public void run() {
            setAfk(true);
        }
    };

    public User(Player player, boolean isAfk) {
        this.player = player;
        this.isAfk = isAfk;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isAfk() {
        return isAfk;
    }

    public void setAfk(boolean isAfk) {
        this.isAfk = isAfk;
        player.sendMessage(ChatColor.GRAY + "You are now " + (isAfk ? "" : "not ") + "afk.");
        notifyEveryone();
    }

    public void cancelAfkTimer() {
        t.cancel();
        afkTimerTask.cancel();
    }

    public void cancelAfkTask() {
        afkTimerTask.cancel();
    }

    public void startAfkTimer(int interval, ServerStatus serverStatus) {
        if (interval < 1) return;
        afkTimerTask.cancel();
        afkTimerTask = new TimerTask() {
            @Override
            public void run() {
                setAfk(true);
                serverStatus.update();
            }
        };
        t.schedule(afkTimerTask, interval * 1000L);
    }

    private void notifyEveryone() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getUniqueId().equals(player.getUniqueId())) {
                p.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + ChatColor.GRAY + " is now " + (isAfk ? "" : "not ") + "afk");
            }
        }
    }
}
