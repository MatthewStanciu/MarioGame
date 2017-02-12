package net.extrillius.blocktest;

import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by TechBug2012 on 2/11/17.
 */
public class PowerupTimer extends BukkitRunnable {
    public Boolean fire = false;
    @Override
    public void run() {
        fire = false;
    }
}
