package me.dueris.genesismc.core.choosing;

import me.dueris.api.events.OriginChooseEvent;
import me.dueris.api.events.OriginEvent;
import me.dueris.genesismc.core.items.OrbOfOrigins;
import org.bukkit.*;
import org.bukkit.entity.Player;

import static me.dueris.genesismc.core.items.OrbOfOrigins.orb;
import static org.bukkit.ChatColor.AQUA;

public class DefaultChoose {

    public static void DefaultChoose(Player p) {

        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 9);

        //default choose
        p.closeInventory();
        p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 9);
        p.sendMessage(AQUA + "You have chosen an origin!");
        p.spawnParticle(Particle.CLOUD, p.getLocation(), 100);
        p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation(), 6);
        p.setCustomNameVisible(false);
        p.getScoreboardTags().add("chosen");
        p.setGameMode(GameMode.SURVIVAL);
        p.setHealthScaled(false);

        Location loc = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 1, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch());
        p.teleportAsync(loc);

        if (p.getScoreboardTags().contains("choosing")) {
            p.removeScoreboardTag("choosing");
        }
        // DO NOT UNCOMMENT, DUERIS SAID SHE'D FIX IT WHEN SHE'S LESS TIRED AND THIS ERROR TOOK AN HOUR TO FIND
        //Bukkit.getServer().getPluginManager().callEvent(new OriginChooseEvent(p));

        if (p.getInventory().getItemInMainHand().isSimilar(OrbOfOrigins.orb)) {
            int amt = p.getInventory().getItemInMainHand().getAmount();
            p.getInventory().getItemInMainHand().setAmount(amt - 1);
        } else {
            if (p.getInventory().getItemInOffHand().isSimilar(orb)) {
                int amt = p.getInventory().getItemInOffHand().getAmount();
                p.getInventory().getItemInOffHand().setAmount(amt - 1);
            }
        }

    }
}
