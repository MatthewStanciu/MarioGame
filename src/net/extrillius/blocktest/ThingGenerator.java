package net.extrillius.blocktest;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by mattbstanciu on 2/2/17.
 */
public class ThingGenerator extends JavaPlugin {
    public Map<Integer, String> items = new HashMap<>();

    public String generateThing() {

        String[] powerups = {"bigmushroom", "1upmushroom", "fireflower"}; //defaults
        int value = powerups.length;

        for (int i = 0; i <= value; i++) {
            items.put(value, powerups[value]);
        }
        getConfig().set("items", items);

        Random r = new Random();
        int n = r.nextInt(powerups.length);
        return powerups[n];
    }
    public String getThing() {
        return generateThing();
    }
}
