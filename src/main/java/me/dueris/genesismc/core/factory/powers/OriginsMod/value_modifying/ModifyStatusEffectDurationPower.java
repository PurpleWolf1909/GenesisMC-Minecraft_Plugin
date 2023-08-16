package me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.function.BinaryOperator;

import static me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler.getOperationMappingsFloat;
import static me.dueris.genesismc.core.factory.powers.OriginsMod.value_modifying.ValueModifyingSuperClass.modify_effect_duration;

public class ModifyStatusEffectDurationPower implements Listener {
    @EventHandler
    public void run(EntityPotionEffectEvent e){
        if(e.getEntity() instanceof Player p){
            if(!modify_effect_duration.contains(p)) return;
            for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
                if(origin.getPowerFileFromType("origins:modify_status_effect_duration").get("status_effect", null) != null){
                    if(e.getNewEffect().getType().equals(PotionEffectType.getByName(origin.getPowerFileFromType("origins:modify_status_effect_duration").get("status_effect", null)))){
                        PotionEffect effect = e.getNewEffect();
                        Float value = Float.valueOf(origin.getPowerFileFromType("origins:modify_status_effect_duration").getModifier().get("value").toString());
                        String operation = origin.getPowerFileFromType("origins:modify_status_effect_duration").getModifier().get("operation").toString();
                        BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = (float) mathOperator.apply(effect.getDuration(), value);
                            effect.withDuration(Math.toIntExact(Long.valueOf(String.valueOf(result))));
                        }
                    }
                }else{
                    for(PotionEffect effect : p.getActivePotionEffects()){
                        Float value = Float.valueOf(origin.getPowerFileFromType("origins:modify_status_effect_duration").getModifier().get("value").toString());
                        String operation = origin.getPowerFileFromType("origins:modify_status_effect_duration").getModifier().get("operation").toString();
                        BinaryOperator mathOperator = getOperationMappingsFloat().get(operation);
                        if (mathOperator != null) {
                            float result = (float) mathOperator.apply(effect.getDuration(), value);
                            effect.withDuration(Math.toIntExact(Long.valueOf(String.valueOf(result))));
                        }
                    }
                }

            }
        }
    }
}