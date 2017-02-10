package net.extrillius.blocktest;

import java.util.Random;

/**
 * Created by mattbstanciu on 2/2/17.
 */
public class ThingGenerator {

    public String generateThing() {

        String[] powerups = {"bigmushroom", "1upmushroom", "fireflower"}; //defaults

        Random r = new Random();
        int n = r.nextInt(powerups.length);
        return powerups[n];
    }
}
