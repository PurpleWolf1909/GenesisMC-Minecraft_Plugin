package me.dueris.genesismc.factory.powers.player.damage;

import me.dueris.genesismc.entity.OriginPlayerUtils;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.utils.OriginContainer;
import me.dueris.genesismc.utils.PowerContainer;
import me.dueris.genesismc.utils.Utils;
import me.dueris.genesismc.utils.translation.LangConfig;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.DamageCommand;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class DamageOverTime extends CraftPower implements Listener{

    private final String damage_type;
    private Long interval;
    private int damage;
    private DamageSource damage_source;
    private double protection_effectiveness;
    private final int ticksE;

    public DamageOverTime() {
        this.interval = 20L;
        this.ticksE = 0;
        this.damage_type = "origins:damage_over_time";
        this.protection_effectiveness = 1.0;
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
    // Death msg look funny lol. "death.attack.hurt_by_water" LMFAO

    @EventHandler
    public void erk(PlayerDeathEvent e){
        if(e.getDeathMessage().equals("death.attack.hurt_by_water")){
            if(e.getPlayer().getName().equals("Optima1")){ // for context, he helped test this a lot for hours so im givin him a lil easter egg
                e.setDeathMessage("Optima1 got too thirsty");
            }
            e.setDeathMessage("{p} took a bath for too long."
                .replace("{p}", e.getPlayer().getName()));
        }
    }

    public void run(Player p, HashMap<Player, Integer> ticksEMap) {
        ticksEMap.putIfAbsent(p, 0);
        if (getPowerArray().contains(p)) {
            for (OriginContainer origin : OriginPlayerUtils.getOrigin(p).values()) {
                for (PowerContainer power : origin.getMultiPowerFileFromType(getPowerFile())) {
                    if (power == null) continue;
                    if (power.getInterval() == null) {
                        Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.burn"));
                        return;
                    }
                    interval = power.getInterval();

                    int ticksE = ticksEMap.getOrDefault(p, 0);
                    if (ticksE < interval) {
                        ticksE++;

                        ticksEMap.put(p, ticksE);
                        return;
                    } else {
                        if (p.getWorld().getDifficulty().equals(Difficulty.EASY)) {
                            if (power.get("damage_easy", power.get("damage", "1")) == null) {
                                damage = Integer.parseInt(power.get("damage", "1"));
                            } else {
                                damage = Integer.parseInt(power.get("damage_easy", power.get("damage", "1")));
                            }
                        } else {
                            damage = Integer.parseInt(power.get("damage", "1"));
                        }

                        protection_effectiveness = Double.parseDouble(power.get("protection_effectiveness", "1"));
                        ConditionExecutor executor = me.dueris.genesismc.GenesisMC.getConditionExecutor();
                        if (executor.check("condition", "conditions", p, power, getPowerFile(), p, null, null, null, p.getItemInHand(), null)) {
                            setActive(p, power.getTag(), true);

                            if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                                float helemt_modifier = 0;
                                float chestplate_modifier = 0;
                                float leggins_modifier = 0;
                                float boots_modifier = 0;
                                float prot1 = (float) protection_effectiveness;
                                float prot2 = (float) protection_effectiveness;
                                float prot3 = (float) protection_effectiveness;
                                float prot4 = (float) protection_effectiveness;
                                if (p.getInventory().getHelmet() != null) {
                                    if (p.getInventory().getHelmet().getLore() != null) {
                                        if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                            helemt_modifier = prot1;
                                        } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                            helemt_modifier = prot2;
                                        } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                            helemt_modifier = prot3;
                                        } else if (p.getEquipment().getHelmet().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                            helemt_modifier = prot4;
                                        } else {
                                            helemt_modifier = 0;
                                        }
                                    }
                                }
                                if (p.getInventory().getChestplate() != null) {

                                    if (p.getInventory().getChestplate().getLore() != null) {
                                        if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                            chestplate_modifier = prot1;
                                        } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                            chestplate_modifier = prot2;
                                        } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                            chestplate_modifier = prot3;
                                        } else if (p.getEquipment().getChestplate().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                            chestplate_modifier = prot4;
                                        } else {
                                            chestplate_modifier = 0;
                                        }
                                    }
                                }
                                if (p.getInventory().getLeggings() != null) {
                                    if (p.getInventory().getLeggings().getLore() != null) {
                                        if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                            leggins_modifier = prot1;
                                        } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                            leggins_modifier = prot2;
                                        } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                            leggins_modifier = prot3;
                                        } else if (p.getEquipment().getLeggings().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                            leggins_modifier = prot4;
                                        } else {
                                            leggins_modifier = 0;
                                        }
                                    }
                                }
                                if (p.getInventory().getBoots() != null) {
                                    if (p.getInventory().getBoots().getLore() != null) {
                                        if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection I")) {
                                            boots_modifier = prot1;
                                        } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection II")) {
                                            boots_modifier = prot2;
                                        } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection III")) {
                                            boots_modifier = prot3;
                                        } else if (p.getEquipment().getBoots().getLore().contains(ChatColor.GRAY + "Water Protection IV")) {
                                            boots_modifier = prot4;
                                        } else {
                                            boots_modifier = 0;
                                        }
                                    }
                                }
                                float basedamage = damage - helemt_modifier - chestplate_modifier - leggins_modifier - boots_modifier;

                                if (p.getHealth() >= basedamage && p.getHealth() != 0 && p.getHealth() - basedamage != 0) {
                                    String namespace;
                                    String key;
                                    if(power.get("damage_type") != null){
                                        if(power.get("damage_type").contains(":")){
                                            namespace = power.get("damage_type").split(":")[0];
                                            key = power.get("damage_type").split(":")[1];
                                        }else{
                                            namespace = "minecraft";
                                            key = power.get("damage_type");
                                        }
                                    }else{
                                        namespace = "minecraft";
                                        key = "generic";
                                    }
                                    DamageType dmgType = Utils.DAMAGE_REGISTRY.get(new ResourceLocation(namespace, key));
                                    ServerPlayer serverPlayer = ((CraftPlayer) p).getHandle();
                                    serverPlayer.hurt(Utils.getDamageSource(dmgType), basedamage);

                                    Random random = new Random();

                                    int r = random.nextInt(3);
                                    if (r == 1) {
                                        if (p.getInventory().getHelmet() != null) {
                                            int heldur = p.getEquipment().getHelmet().getDurability();
                                            p.getEquipment().getHelmet().setDurability((short) (heldur + 3));
                                        }
                                        if (p.getInventory().getChestplate() != null) {
                                            int chestdur = p.getEquipment().getChestplate().getDurability();
                                            p.getEquipment().getChestplate().setDurability((short) (chestdur + 3));
                                        }
                                        if (p.getInventory().getLeggings() != null) {
                                            int legdur = p.getEquipment().getLeggings().getDurability();
                                            p.getEquipment().getLeggings().setDurability((short) (legdur + 3));
                                        }
                                        if (p.getInventory().getBoots() != null) {
                                            int bootdur = p.getEquipment().getBoots().getDurability();
                                            p.getEquipment().getBoots().setDurability((short) (bootdur + 3));
                                        }

                                    }
                                } else if (p.getHealth() <= basedamage && p.getHealth() != 0) {
                                    p.setHealth(0.0f);
                                }
                            }

                        } else {
                            setActive(p, power.getTag(), false);
                        }

                        ticksEMap.put(p, 0);
                    }
                }
            }
        }
    }
    
    @Override
    public void run(Player p) {
//        if (damage_over_time.contains(p)) {
//            for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
//                PowerContainer power = origin.getPowerFileFromType("origins:damage_over_time");
//                if (power == null) continue;
//                if (power.getInterval() == null) {
//                    Bukkit.getLogger().warning(LangConfig.getLocalizedString(p, "powers.errors.damageOverTime"));
//                    return;
//                }
//                interval = power.getInterval();
//                if (ticksE < interval) {
//                    ticksE++;
//                    return;
//                } else {
//
//                    ticksE = 0;
//                }
//            }
//        }
        //removed old code to use new OriginScheduler
    }

    @Override
    public String getPowerFile() {
        return "origins:damage_over_time";
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return damage_over_time;
    }
}