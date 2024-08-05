package me.dueris.originspaper.factory.condition.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.data.ApoliDataTypes;
import me.dueris.originspaper.data.types.Comparison;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnchantmentCondition {

	public static boolean condition(@NotNull DeserializedFactoryJson data, @NotNull Tuple<Level, ItemStack> worldAndStack) {

		ResourceKey<Enchantment> enchantmentKey = data.get("enchantment");
		Holder<Enchantment> enchantment = enchantmentKey == null ? null : worldAndStack.getA().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
			.getHolder(enchantmentKey)
			.orElseThrow();

		Comparison comparison = data.get("comparison");
		int compareTo = data.get("compare_to");

		ItemEnchantments component = worldAndStack.getB().getEnchantments();
		int level = enchantment != null ? component.getLevel(enchantment)
			: component.keySet().size();
		return comparison.compare(level, compareTo);

	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("enchantment"),
			InstanceDefiner.instanceDefiner()
				.add("enchantment", SerializableDataTypes.ENCHANTMENT, null)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN)
				.add("compare_to", SerializableDataTypes.INT, 0)
				.add("use_modifications", SerializableDataTypes.BOOLEAN, true),
			EnchantmentCondition::condition
		);
	}

}