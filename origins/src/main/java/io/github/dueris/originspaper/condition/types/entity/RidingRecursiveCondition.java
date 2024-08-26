package io.github.dueris.originspaper.condition.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Comparison;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class RidingRecursiveCondition {

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("riding_recursive"),
			SerializableData.serializableData()
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
				.add("compare_to", SerializableDataTypes.INT, 1),
			(data, entity) -> {
				int count = 0;
				if (entity.isPassenger()) {
					Predicate<Tuple<Entity, Entity>> cond = data.get("bientity_condition");
					Entity vehicle = entity.getVehicle();
					while (vehicle != null) {
						if (cond == null || cond.test(new Tuple<>(entity, vehicle))) {
							count++;
						}
						vehicle = vehicle.getVehicle();
					}
				}
				return ((Comparison) data.get("comparison")).compare(count, data.getInt("compare_to"));
			}
		);
	}
}
