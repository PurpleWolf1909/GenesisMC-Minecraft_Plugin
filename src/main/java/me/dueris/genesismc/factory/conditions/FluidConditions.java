package me.dueris.genesismc.factory.conditions;

import me.dueris.genesismc.factory.TagRegistryParser;
import net.minecraft.world.item.Items;
import org.bukkit.Fluid;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftFluid;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.util.Optional;

import static me.dueris.genesismc.factory.conditions.ConditionExecutor.getResult;

public class FluidConditions implements Condition {
    @Override
    public String condition_type() {
        return "FLUID_CONDITION";
    }

    @Override
    public Optional<Boolean> check(JSONObject condition, Entity actor, Entity target, Block block, Fluid fluid, ItemStack itemStack, EntityDamageEvent entityDamageEvent) {
        if (condition.isEmpty()) return Optional.empty();
        if (condition.get("type") == null) return Optional.empty();
        if (fluid == null) return Optional.empty();
        boolean inverted = (boolean) condition.getOrDefault("inverted", false);
        String type = condition.get("type").toString().toLowerCase();
        net.minecraft.world.level.material.Fluid flNMS = CraftFluid.bukkitToMinecraft(fluid);
        switch (type) {
            case "apoli:empty" -> {
                return getResult(inverted, Optional.of(flNMS.defaultFluidState().isEmpty()));
            }
            case "apoli:in_tag" -> {
                for (String flu : TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString())) {
                    if (flu == null) continue;
                    if (fluid == null) continue;
                    return getResult(inverted, Optional.of(flu.equalsIgnoreCase(fluid.toString())));
                }
                return getResult(inverted, Optional.of(false));
            }
            case "apoli:still" -> {
                return getResult(inverted, Optional.of(flNMS.defaultFluidState().isSource()));
            }
            case "apoli:fluid" -> {
                return getResult(inverted, Optional.of(fluid.getKey().equals(NamespacedKey.fromString(condition.get("fluid").toString()))));
            }
            default -> {
                return getResult(inverted, Optional.empty());
            }
        }
    }
}
