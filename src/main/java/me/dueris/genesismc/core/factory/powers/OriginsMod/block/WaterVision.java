package me.dueris.genesismc.core.factory.powers.OriginsMod.block;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.block.WaterBreathe.isInBreathableWater;
import static me.dueris.genesismc.core.factory.powers.Powers.water_vision;

public class WaterVision extends BukkitRunnable {
    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (water_vision.contains(p)) {
                if (isInBreathableWater(p)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 15, 3, false, false));
                }
            }
        }
    }
}
