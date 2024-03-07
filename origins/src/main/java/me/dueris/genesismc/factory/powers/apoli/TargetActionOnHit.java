package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.CooldownUtils;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TargetActionOnHit extends CraftPower implements Listener {

    @Override
    public void run(Player p) {

    }

    @EventHandler
    public void s(EntityDamageByEntityEvent e) {
        Entity actor = e.getDamager();
        Entity target = e.getEntity();

        if (!(actor instanceof Player player)) return;
        if (!getPowerArray().contains(actor)) return;

        for (Layer layer : CraftApoli.getLayersFromRegistry()) {
            for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(player, getPowerFile(), layer)) {
                if (CooldownUtils.isPlayerInCooldownFromTag(player, Utils.getNameOrTag(power))) continue;
                if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) player) &&
                ConditionExecutor.testDamage(power.get("damage_condition"), e) &&
                ConditionExecutor.testEntity(power.get("target_condition"), (CraftEntity) e.getEntity())) {
                    setActive(player, power.getTag(), true);
                    Actions.EntityActionType(target, power.getEntityAction());
                    if (power.getObjectOrDefault("cooldown", 1) != null) {
                        CooldownUtils.addCooldown((Player) actor, Utils.getNameOrTag(power), power.getType(), power.getIntOrDefault("cooldown", power.getIntOrDefault("max", 1)), power.get("hud_render"));
                    }
                } else {
                    setActive(player, power.getTag(), false);
                }
            }
        }
    }

    @Override
    public String getPowerFile() {
        return "apoli:target_action_on_hit";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return target_action_on_hit;
    }

    @Override
    public List<FactoryObjectInstance> getValidObjectFactory() {
        return super.getDefaultObjectFactory(List.of(
            new FactoryObjectInstance("entity_action", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("cooldown", Integer.class, 1),
            new FactoryObjectInstance("hud_render", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("damage_condition", JSONObject.class, new JSONObject()),
            new FactoryObjectInstance("target_condition", JSONObject.class, new JSONObject())
        ));
    }
}
