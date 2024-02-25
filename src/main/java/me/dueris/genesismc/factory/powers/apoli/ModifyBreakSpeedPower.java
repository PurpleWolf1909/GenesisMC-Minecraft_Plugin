package me.dueris.genesismc.factory.powers.apoli;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

import static me.dueris.genesismc.factory.powers.apoli.superclass.ValueModifyingSuperClass.modify_break_speed;

public class ModifyBreakSpeedPower extends CraftPower implements Listener {

    String MODIFYING_KEY = "modify_break_speed";

    @Override
    public void setActive(Player p, String tag, Boolean bool) {
        if (powers_active.containsKey(p)) {
            if (powers_active.get(p).containsKey(tag)) {
                powers_active.get(p).replace(tag, bool);
            } else {
                powers_active.get(p).put(tag, bool);
            }
        } else {
            powers_active.put(p, new HashMap());
            setActive(p, tag, bool);
        }
    }

    public int calculateHasteAmplifier(float value) {
        float maxValue = 10000.0f;
        float minValue = 0.1f;
        int maxAmplifier = 10;
        int minAmplifier = 0;

        float normalizedValue = Math.max(minValue, Math.min(value, maxValue));
        float percentage = (normalizedValue - minValue) / (maxValue - minValue);
        int amplifier = (int) (percentage * (maxAmplifier - minAmplifier)) + minAmplifier;

        return amplifier + 1;
    }

    @EventHandler
    public void swing(PlayerArmSwingEvent e) {
        Player p = e.getPlayer();
        if (modify_break_speed.contains(p)) {
            if (p.getGameMode().equals(GameMode.CREATIVE)) return;
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
                try {
                    ConditionExecutor conditionExecutor = GenesisMC.getConditionExecutor();
                    for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                        if (ConditionExecutor.testEntity(power.get("condition"), (CraftEntity) p) && ConditionExecutor.testBlock(power.get("block_condition"), (CraftBlock) p.getTargetBlockExact(Math.toIntExact(Math.round(AttributeHandler.Reach.getFinalReach(p)))))) {
                            setActive(p, power.getTag(), true);
                            // if(power.getPossibleModifiers("modifier", "modifiers"))
                            for (HashMap<String, Object> modifier : power.getPossibleModifiers("modifier", "modifiers")) {
                                if (Float.valueOf(modifier.get("value").toString()) <= 0) {
                                    // Slower mine
                                    p.addPotionEffect(
                                            new PotionEffect(
                                                    PotionEffectType.SLOW_DIGGING,
                                                    20,
                                                    (Math.round(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY)) + 1) * 17,
                                                    false, false, false
                                            )
                                    );
                                } else {
                                    // Speed up
                                    p.addPotionEffect(
                                            new PotionEffect(
                                                    PotionEffectType.FAST_DIGGING,
                                                    20,
                                                    (Math.round(valueModifyingSuperClass.getPersistentAttributeContainer(p, MODIFYING_KEY)) + 1) * 17,
                                                    false, false, false
                                            )
                                    );
                                }
                            }
                        } else {
                            setActive(p, power.getTag(), false);
                        }
                    }
                } catch (Exception ev) {
                    ev.printStackTrace();
                }
            }
        }
    }

    public void apply(Player p) {
        ValueModifyingSuperClass valueModifyingSuperClass = new ValueModifyingSuperClass();
        if (modify_break_speed.contains(p)) {
            for (Layer layer : CraftApoli.getLayersFromRegistry()) {
                for (Power power : OriginPlayerAccessor.getMultiPowerFileFromType(p, getPowerFile(), layer)) {
                    for (HashMap<String, Object> modifier : power.getJsonListSingularPlural("modifier", "modifiers")) {
                        Float value = Float.valueOf(modifier.get("value").toString());
                        valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, value); // Why does there need to be a binary operator if the operator does nothing?
                    }
                }
            }
        } else {
            valueModifyingSuperClass.saveValueInPDC(p, MODIFYING_KEY, valueModifyingSuperClass.getDefaultValue(MODIFYING_KEY));
        }
    }

    @Override
    public void run(Player p) {

    }

    @Override
    public String getPowerFile() {
        return "apoli:modify_break_speed";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return modify_break_speed;
    }
}
