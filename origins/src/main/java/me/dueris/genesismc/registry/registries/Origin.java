package me.dueris.genesismc.registry.registries;

import me.dueris.calio.builder.inst.FactoryInstance;
import me.dueris.calio.builder.inst.FactoryObjectInstance;
import me.dueris.calio.builder.inst.factory.FactoryBuilder;
import me.dueris.calio.builder.inst.factory.FactoryElement;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registerable;
import me.dueris.calio.registry.Registrar;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.CraftApoli;
import me.dueris.genesismc.registry.Registries;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Origin extends FactoryJsonObject implements Serializable, FactoryInstance {

    @Serial
    private static final long serialVersionUID = 1L;

    NamespacedKey tag;
    ArrayList<Power> powerContainer;
    FactoryJsonObject choosingCondition;
    FactoryJsonObject factory;

    public Origin(boolean toRegistry) {
        super(null);
        if (!toRegistry) {
            throw new RuntimeException("Invalid constructor used.");
        }
    }

    /**
     * An object that stores an origin and all the details about it.
     *
     * @param tag            The origin tag.
     * @param powerContainer An array of powers that the origin has.
     */
    public Origin(NamespacedKey tag, ArrayList<Power> powerContainer, FactoryJsonObject factoryJsonObject) {
        super(factoryJsonObject.handle);
        this.tag = tag;
        this.powerContainer = powerContainer;
        this.factory = factoryJsonObject;
    }

    /**
     * @return The customOrigin formatted for debugging, not to be used in other circumstances.
     */
    @Override
    public String toString() {
        return "Tag: " + this.tag + ", PowerContainer: " + this.powerContainer.toString();
    }

    public boolean getUsesCondition() {
        return this.choosingCondition != null;
    }

    public void setUsesCondition(FactoryJsonObject condition) {
        this.choosingCondition = condition;
    }

    @Override
    public NamespacedKey getKey() {
        return this.tag;
    }

    /**
     * @return The origin tag.
     */
    public String getTag() {
        return this.tag.asString();
    }

    /**
     * @return An array containing all the origin powers.
     */
    public ArrayList<Power> getPowerContainers() {
        return new ArrayList<>(this.powerContainer);
    }

    /**
     * @return The name of the origin.
     */
    public String getName() {
        return getStringOrDefault("name", "No Name");
    }

    /**
     * @return The description for the origin.
     */
    public String getDescription() {
        return getStringOrDefault("name", "No Description");
    }

    /**
     * @return An array of powers from the origin.
     */
    public List<String> getPowers() {
        return isPresent("powers") ? getJsonArray("powers").asList().stream().map(FactoryElement::getString).toList() : new ArrayList<>();
    }

    /**
     * @return The icon of the origin.
     */
    public String getIcon() {
        return getItemStack("icon").getType().getKey().asString();
//        Object value = this.originFile.get("icon");
//        try {
//            if (((JSONObject) value).get("item") != "minecraft:air") return (String) ((JSONObject) value).get("item");
//            else return "minecraft:player_head";
//        } catch (Exception e) {
//            try {
//                if (!value.toString().equals("minecraft:air")) return value.toString();
//                else return "minecraft:player_head";
//            } catch (Exception ex) {
//                return "minecraft:player_head";
//            }
//        }
    }

    public int getOrder() {
        return !isPresent("order") ? 5 : getNumber("order").getInt();
    }

    /**
     * @return The icon as a Material Object.
     */
    public Material getMaterialIcon() {
        return me.dueris.calio.util.MiscUtils.getBukkitMaterial(getIcon());
    }

    /**
     * @return The impact of the origin.
     */
    public long getImpact() {
        return getNumberOrDefault("impact", 0).getLong();
    }

    /**
     * @return If the origin is choose-able from the choose menu.
     */
    public boolean getUnchooseable() {
        return getBooleanOrDefault("unchoosable", false);
    }

    /**
     * @return The PowerContainer with the given type if present in the origin.
     */
    public ArrayList<Power> getMultiPowerFileFromType(String powerType) {
        ArrayList<Power> powers = new ArrayList<>();
        for (Power power : getPowerContainers()) {
            if (power == null) continue;
            if (power.getType().equals(powerType)) powers.add(power);
        }
        return powers;
    }

    public Power getSinglePowerFileFromType(String powerType) {
        for (Power power : getPowerContainers()) {
            if (power.getType().equals(powerType)) return power;
        }
        return null;
    }

    @Override
    public List<FactoryObjectInstance> getValidObjectFactory() {
        return List.of(
            new FactoryObjectInstance("name", String.class, "No Name"),
            new FactoryObjectInstance("icon", ItemStack.class, new ItemStack(Material.PLAYER_HEAD, 1)),
            new FactoryObjectInstance("impact", Integer.class, 0),
            new FactoryObjectInstance("unchooseable", Boolean.class, false),
            new FactoryObjectInstance("powers", JSONArray.class, new JSONArray())
        );
    }

    @Override
    public void createInstance(FactoryBuilder obj, File rawFile, Registrar<? extends Registerable> registry, NamespacedKey namespacedTag) {
        Registrar<Origin> registrar = (Registrar<Origin>) registry;
        try {
            ArrayList<Power> containers = new ArrayList<>();
            for (String element : obj.getRoot().getJsonArray("powers").asList().stream().map(FactoryElement::getString).toList()) {
                if (((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).rawRegistry.containsKey(NamespacedKey.fromString(element))) {
                    containers.add(((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(element)));
                }

                for (Power power : CraftApoli.getNestedPowers(((Registrar<Power>) GenesisMC.getPlugin().registry.retrieve(Registries.POWER)).get(NamespacedKey.fromString(element)))) {
                    if (power != null) {
                        containers.add(power);
                    }
                }
            }
            registrar.register(new Origin(namespacedTag, containers,obj.getRoot()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
