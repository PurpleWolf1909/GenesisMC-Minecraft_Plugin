package me.dueris.genesismc.factory.powers.actions;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.entity.OriginPlayer;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class ActionOnBlockBreak extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void brek(BlockBreakEvent e) {
        Player actor = e.getPlayer();

        if (!getPowerArray().contains(actor)) return;

        for (OriginContainer origin : OriginPlayer.getOrigin(actor).values()) {
            for (PowerContainer powerContainer : origin.getMultiPowerFileFromType(getPowerFile())) {
                PowerContainer power = powerContainer;
                if (power == null) continue;

                setActive(actor, powerContainer.getTag(), true);
                Actions.BlockActionType(e.getBlock().getLocation(), power.getBlockAction());
                Actions.EntityActionType(e.getPlayer(), power.getEntityAction());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!getPowerArray().contains(e.getPlayer())) return;
                        setActive(actor, powerContainer.getTag(), false);
                    }
                }.runTaskLater(GenesisMC.getPlugin(), 2L);
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "origins:action_on_block_break";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return action_on_block_break;
    }

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if(powers_active.containsKey(p)){
            if(powers_active.get(p).containsKey(tag)){
                powers_active.get(p).replace(tag, bool);
            }else{
                powers_active.get(p).put(tag, bool);
            }
        }else{
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }
}
