package net.extrillius.blocktest;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by mattbstanciu on 1/26/17.
 */
//todo flag jump
//todo powerup timer
public class Mario extends JavaPlugin implements Listener {
    public Boolean extraLife = false;
    private PowerupTimer timer = new PowerupTimer();

    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void move(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location l = p.getLocation();
        Block b = l.add(0,2,0).getBlock();

        if (p.getLocation().subtract(0, 0.001, 0).getBlock().getType() == Material.AIR) {
            if (b.getY() - p.getEyeLocation().getY() <= 1 && b.getType() == Material.GOLD_BLOCK) {
                b.setType(Material.BRICK);

                ThingGenerator generator = new ThingGenerator();
                String thing = generator.generateThing();
                Material item;
                switch (thing) {
                    case "bigmushroom":
                        item = Material.RED_MUSHROOM;
                        break;
                    case "1upmushroom":
                        item = Material.BROWN_MUSHROOM;
                        break;
                    case "fireflower":
                        item = Material.YELLOW_FLOWER;
                        break;
                    default:
                        item = Material.AIR;
                        break;
                }
                p.getWorld().dropItem(l.add(0,3,0), new ItemStack(item));
            }
        }

        if (p.getInventory().contains(Material.RED_MUSHROOM)) {
            p.getInventory().remove(Material.RED_MUSHROOM);
            p.setMaxHealth(40);
            p.setHealth(40);
            p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 10, 1);
        }
        else if (p.getInventory().contains(Material.BROWN_MUSHROOM)) {
            p.getWorld().playSound(p.getLocation(), Sound.LEVEL_UP, 10, 1);
            final Sound oneUp = Sound.LEVEL_UP;
            final World world = p.getWorld();
            final Location loc = p.getLocation();
            getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    world.playSound(loc, oneUp, 10, 1);
                }
            }, 10L);
            extraLife = true;
            p.getInventory().remove(Material.BROWN_MUSHROOM);
        }
        else if (p.getInventory().contains(Material.YELLOW_FLOWER)) {
            p.getWorld().playSound(p.getLocation(), Sound.GHAST_FIREBALL, 10, 1);
            p.getInventory().remove(Material.YELLOW_FLOWER);
            timer.fire = true;
            new PowerupTimer().runTaskTimer(this, 0L, 100L);
        }

        if (extraLife) {
            p.setMaxHealth(40);
            p.setHealth(40);
        }

        if (p.getHealth() <= 20 && p.getMaxHealth() == 40) {
            p.setMaxHealth(20);
        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Entity damager = e.getDamager();
            Entity damaged = e.getEntity();
            if (timer.fire) {
                damaged.setFireTicks(100);
            }
        }
    }

    @EventHandler
    public void shootFire(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_AIR) {
            if (timer.fire) {
                Fireball f = p.launchProjectile(Fireball.class);
                f.setVelocity(p.getLocation().getDirection().multiply(2));
                f.setIsIncendiary(false);
                f.setYield(0);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        final Player p = e.getEntity();
        if (extraLife) {
            getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    if (p.isDead()) {
                        p.setMaxHealth(20);
                        p.setHealth(20);
                        extraLife = false;
                    }
                }
            });
        }
        else {
            extraLife = false;
            timer.fire = false;
            p.setMaxHealth(20);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("disablepowerups")) { //temporary until I add other ways to deactivate powerups
            timer.fire = false;
            extraLife = false;
            p.setMaxHealth(20);
            p.setHealth(20);
            p.sendMessage(ChatColor.GREEN + "Powerups disabled");
        }
        if (cmd.getName().equalsIgnoreCase("powerupstatus")) {
            StringBuilder builder = new StringBuilder();

            if (timer.fire) {
                builder.append("fire ");
            }
            else if (extraLife) {
                builder.append("1up ");
            }
            else if (p.getHealth() > 20) {
                builder.append("redmushroom ");
            }
            else {
                p.sendMessage(ChatColor.GREEN + "nothing");
            }
            p.sendMessage(builder.toString());
        }
        return true;
    }
}
