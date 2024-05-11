package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.ArrayList;

public class DisableRegeneration extends CraftPower implements Listener {

	@EventHandler
	public void disable(EntityRegainHealthEvent e) {
		if (e.getEntity() instanceof Player p) {
			if (disable_regen.contains(p)) {
				for (Power power : OriginPlayerAccessor.getPowers(p, getType())) {
					if (ConditionExecutor.testEntity(power.getJsonObject("condition"), (CraftEntity) p)) {
						setActive(p, power.getTag(), true);
						if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
							e.setAmount(0);
							e.setCancelled(true);
						}
					} else {
						setActive(p, power.getTag(), false);
					}
				}
			}
		}
	}

	@Override
	public String getType() {
		return "apoli:disable_regen";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return disable_regen;
	}
}
