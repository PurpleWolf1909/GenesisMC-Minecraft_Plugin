package io.github.dueris.originspaper.condition.types.item;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EnchantableCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().isEnchantable();
	}

	public static @NotNull ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("enchantable"),
			SerializableData.serializableData(),
			EnchantableCondition::condition
		);
	}

}
