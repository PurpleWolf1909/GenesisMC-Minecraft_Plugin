package me.dueris.genesismc.core.factory.powers.block;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

import static me.dueris.genesismc.core.factory.powers.Powers.webbing;
import static org.bukkit.Material.AIR;
import static org.bukkit.Material.COBWEB;

public class Webbing implements Listener {

    private static HashMap<UUID, Boolean> canWeb =new HashMap<>();

    @EventHandler
    public void WebMaster(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = (Player) e.getDamager();



            if (!canWeb.containsKey(p.getUniqueId())) canWeb.put(p.getUniqueId(), Boolean.TRUE);
            if (!canWeb.get(p.getUniqueId())) return;
            if (webbing.contains(OriginPlayer.getOriginTag(p))) {
                Location loc = e.getEntity().getLocation();
                Block b = loc.getBlock();
                canWeb.replace(p.getUniqueId(), Boolean.FALSE);
                b.setType(COBWEB);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        canWeb.replace(p.getUniqueId(), Boolean.TRUE);
                        if(b.getType() == AIR || b.getType() == COBWEB){
                            b.setType(AIR);
                            this.cancel();
                        }else{
                            this.cancel();
                        }
                    }

                }.runTaskTimer(GenesisMC.getPlugin(), 40L, 5L);
            }
        }
    }
}


