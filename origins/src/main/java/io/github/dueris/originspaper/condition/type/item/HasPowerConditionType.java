package io.github.dueris.originspaper.condition.type.item;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.storage.ItemPowersComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HasPowerConditionType {

	public static boolean condition(@NotNull ItemStack stack, @Nullable EquipmentSlotGroup slot, ResourceLocation powerId) {
		ItemPowersComponent component = new ItemPowersComponent(stack);
		return component.size() > 0 && component.getReferences().stream().map(ItemPowersComponent.Entry::powerId).toList().contains(powerId);
	}

	public static @NotNull ConditionTypeFactory<Tuple<Level, ItemStack>> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("has_power"),
			new SerializableData()
				.add("slot", SerializableDataTypes.ATTRIBUTE_MODIFIER_SLOT)
				.add("power", SerializableDataTypes.IDENTIFIER),
			(data, worldAndStack) -> condition(worldAndStack.getB(),
				data.get("slot"),
				data.get("power")
			)
		);
	}

}