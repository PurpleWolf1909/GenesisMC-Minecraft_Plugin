package me.dueris.genesismc.core.origins;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class CreepMain implements Listener {

    @EventHandler
    public void OnTarget(EntityTargetEvent e) {
        if (e.getEntity() instanceof Creeper && (e.getTarget() instanceof Player)) {

            Player p = (Player) e.getTarget();
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (origintag.equalsIgnoreCase("genesis:origin-creep")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCreepDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
        if (origintag.equalsIgnoreCase("genesis:origin-creep")) {
            if (e.getEntity().getType() == EntityType.CREEPER) {
                Creeper killer = (Creeper) e.getEntity();
                if (killer.isPowered()) {
                    e.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
                }
            } else if (e.getEntity().getType() == EntityType.PLAYER) {
                Player killerp = e.getEntity();
                PersistentDataContainer datak = killerp.getPersistentDataContainer();
                @Nullable String origintagk = datak.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
                if (origintag.equalsIgnoreCase("genesis:origin-creep")) {
                    if (p.getWorld().isThundering() && e.getEntity().getPersistentDataContainer().has(new NamespacedKey(GenesisMC.getPlugin(), "originid"), PersistentDataType.INTEGER)) {
                        PersistentDataContainer edata = e.getEntity().getPersistentDataContainer();
                        @Nullable String origintage = edata.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
                        if (origintage.equalsIgnoreCase("genesis:origin-creep")) {
                            e.getDrops().add(new ItemStack(Material.CREEPER_HEAD));
                        }


                    }
                }

            }
        }
    }

    @EventHandler
    public void onCreepDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PersistentDataContainer data = p.getPersistentDataContainer();
            @Nullable String origintag = data.get(new NamespacedKey(GenesisMC.getPlugin(), "origintag"), PersistentDataType.STRING);
            if (origintag.equalsIgnoreCase("genesis:origin-creep")) {
                if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    e.setDamage(e.getFinalDamage() - 7);
                } else {
                    if (e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
                        e.setDamage(e.getFinalDamage() + 2);
                    } else {
                        e.setDamage(e.getFinalDamage() + 4);
                    }
                }
            }
        }
    }
}

