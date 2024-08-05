package me.dueris.originspaper.factory.condition.types.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import io.github.dueris.calio.parser.reader.DeserializedFactoryJson;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class CustomDataCondition {

	public static boolean condition(@NotNull DeserializedFactoryJson data, @NotNull Tuple<Level, ItemStack> worldAndStack) {
		return worldAndStack.getB().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).matchedBy(data.get("nbt"));
	}

	public static @NotNull ConditionFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("nbt"),
			InstanceDefiner.instanceDefiner()
				.add("nbt", SerializableDataTypes.NBT),
			CustomDataCondition::condition
		);

	}

}