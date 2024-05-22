package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Modifier;
import me.dueris.genesismc.util.Util;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.BinaryOperator;

public class ModifyEnchantmentLevel extends ModifierPower {
	private final FactoryJsonObject itemCondition;
	private final NamespacedKey enchantment;

	public ModifyEnchantmentLevel(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject modifier, FactoryJsonArray modifiers, FactoryJsonObject itemCondition, NamespacedKey enchantment) {
		super(name, description, hidden, condition, loading_priority, modifier, modifiers);
		this.itemCondition = itemCondition;
		this.enchantment = enchantment;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return ModifierPower.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("modify_enchantment_level"))
			.add("item_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("enchantment", NamespacedKey.class, new RequiredInstance());
	}

	@Override
	public void tick(Player p) {
		HashSet<ItemStack> items = new HashSet<>(Arrays.stream(p.getInventory().getArmorContents()).toList());
		items.add(p.getInventory().getItemInMainHand());
		for (ItemStack item : items) {
			if (!isActive(p)) continue;
			if (!ConditionExecutor.testItem(itemCondition, item)) continue;
			for (Modifier modifier : getModifiers()) {
				Enchantment enchant = Enchantment.getByKey(enchantment);
				if (item.containsEnchantment(enchant)) {
					item.removeEnchantment(enchant);
				}
				int result = 1;
				float value = modifier.value();
				String operation = modifier.operation();
				BinaryOperator mathOperator = Util.getOperationMappingsInteger().get(operation);
				if (mathOperator != null) {
					result = Integer.valueOf(String.valueOf(mathOperator.apply(0, value)));
				}
				if (result < 0) {
					result = 1;
				}
				try {
					item.addEnchantment(enchant, result);
				} catch (Exception e) {
					// ignore. -- cannot apply enchant to itemstack
				}
			}
		}
	}

}
