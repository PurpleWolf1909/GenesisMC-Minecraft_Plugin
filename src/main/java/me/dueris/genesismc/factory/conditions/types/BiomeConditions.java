package me.dueris.genesismc.factory.conditions.types;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.TagRegistryParser;
import me.dueris.genesismc.registry.Registerable;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.Utils;
import me.dueris.genesismc.util.world.BiomeMappings;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftBiome;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiPredicate;

public class BiomeConditions {

    public static HashMap<String, ArrayList<Biome>> biomeTagMappings = new HashMap<>();

    public void prep(){
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, biome) -> {
            if (TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString()) != null) {
                if (!biomeTagMappings.containsKey(condition.get("tag"))) {
                    biomeTagMappings.put(condition.get("tag").toString(), new ArrayList<>());
                    for (String mat : TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString())) {
                        try {
                            biomeTagMappings.get(condition.get("tag")).add(Biome.valueOf(mat.split(":")[1].toUpperCase()));
                        } catch (IllegalArgumentException e) {
                            // akjghdfkj
                        }
                    }
                    return false;
                } else {
                    // mappings exist, now we can start stuff
                    return biomeTagMappings.get(condition.get("tag")).contains(CraftBiome.minecraftToBukkit(biome).getKey().asString().toLowerCase());
                }
            } else {
                return false;
            }
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("precipitation"), (condition, biome) -> {
            return biome.hasPrecipitation() ?
                biome.getBaseTemperature() <= 0.15f ?
                    condition.get("precipitation").toString().equals("snow")
                    : condition.get("precipitation").toString().equals("rain")
            : condition.get("precipitation").toString().equals("none");
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("category"), (condition, biome) -> {
            Biome b = CraftBiome.minecraftToBukkit(biome);
            for(String biS : BiomeMappings.getBiomeIDs(condition.get("category").toString())){
                if(Biome.valueOf(biS.split(":")[1].toUpperCase()).equals(b)){
                    return true;
                }
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("temperature"), (condition, biome) -> {
            String comparison = condition.get("comparison").toString();
            float compare_to = Float.parseFloat(condition.get("compare_to").toString());
            return Utils.compareValues(biome.getBaseTemperature(), comparison, compare_to);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("high_humidity"), (condition, biome) -> {
            return Utils.compareValues(biome.climateSettings.downfall(), ">=", 0.85f);
        }));
    }

    private void register(ConditionFactory factory){
        GenesisMC.getPlugin().registry.retrieve(Registries.BIOME_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, net.minecraft.world.level.biome.Biome> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, net.minecraft.world.level.biome.Biome> test){
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, net.minecraft.world.level.biome.Biome tester){
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}
