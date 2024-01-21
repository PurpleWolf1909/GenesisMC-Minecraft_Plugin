package me.dueris.genesismc.factory.conditions.biEntity;

import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.player.RestrictArmor;
import me.dueris.genesismc.factory.powers.world.EntitySetPower;
import org.bukkit.Bukkit;
import org.bukkit.Fluid;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class BiEntityCondition implements Condition, Listener {

    @Override
    public String condition_type() {
        return "BIENTITY_CONDITION";
    }

    @Override
    public Optional<Boolean> check(JSONObject condition, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        if (actor == null || target == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        switch (type) {
            case "apoli:actor_condition" -> {
                return ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("condition"), actor, target, block, fluid, itemStack, entityDamageEvent);
            }
            case "apoli:target_condition" -> {
                return ConditionExecutor.biEntityCondition.check((JSONObject) condition.get("condition"), target, actor, block, fluid, itemStack, entityDamageEvent);
            }
            case "apoli:attack_target" -> {
                Bukkit.getLogger().warning("apoli:attack_target is depreciated for the plugin, for more details msg Dueris");
            }
            case "apoli:distance" -> {
                @NotNull Vector actorVector = actor.getLocation().toVector();
                @NotNull Vector targetVector = target.getLocation().toVector();
                String comparison = condition.get("comparison").toString();
                double compare_to = Double.parseDouble(condition.get("compare_to").toString());
                return getResult(inverted, Optional.of(RestrictArmor.compareValues(actorVector.distance(targetVector), comparison, compare_to)));
            }
            case "apoli:in_set" -> {
                return getResult(inverted, Optional.of(EntitySetPower.isInEntitySet(target, condition.get("set").toString())));
            }
            case "apoli:can_see" -> {
                if (actor instanceof Player pl) {
                    return getResult(inverted, Optional.of(pl.canSee(target)));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:owner" -> {
                if (target instanceof Tameable tameable) {
                    return getResult(inverted, Optional.of(tameable.getOwner().equals(actor)));
                } else {
                    return getResult(inverted, Optional.of(false));
                }
            }
            case "apoli:riding_recursive" -> {
                return getResult(inverted, Optional.of(actor.getPassengers().contains(target)));
            }
            case "apoli:riding_root" -> {
                for (int i = 0; i < actor.getPassengers().toArray().length; i++) {
                    if (actor.getPassengers().isEmpty()) return getResult(inverted, Optional.of(false));
                    if (actor.getPassengers().get(i) != null) {
                        return getResult(inverted, Optional.of(i == actor.getPassengers().toArray().length));
                    } else {
                        return getResult(inverted, Optional.of(false));
                    }
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:riding" -> {
                return getResult(inverted, Optional.of(target.getPassengers().contains(actor)));
            }
            default -> {
                return getResult(inverted, Optional.empty());
            }
        }
        return getResult(inverted, Optional.empty());
    }
}
