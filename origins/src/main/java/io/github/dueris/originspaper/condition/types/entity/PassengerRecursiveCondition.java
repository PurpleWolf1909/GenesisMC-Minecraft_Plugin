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

import java.util.List;
import java.util.function.Predicate;

public class PassengerRecursiveCondition {

	public static @NotNull ConditionTypeFactory<Entity> getFactory() {
		return new ConditionTypeFactory<>(
			OriginsPaper.apoliIdentifier("passenger_recursive"),
			SerializableData.serializableData()
				.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
				.add("comparison", ApoliDataTypes.COMPARISON, Comparison.GREATER_THAN_OR_EQUAL)
				.add("compare_to", SerializableDataTypes.INT, 1),
			(data, entity) -> {
				int count = 0;
				if (entity.isVehicle()) {
					if (data.isPresent("bientity_condition")) {
						Predicate<Tuple<Entity, Entity>> condition = data.get("bientity_condition");
						List<Entity> passengers = entity.getPassengers();
						count = (int) passengers.stream().flatMap(Entity::getSelfAndPassengers).filter(e -> condition.test(new Tuple<>(e, entity))).count();
					} else {
						count = (int) entity.getPassengers().stream().flatMap(Entity::getSelfAndPassengers).count();
					}
				}
				return ((Comparison) data.get("comparison")).compare(count, data.getInt("compare_to"));
			}
		);
	}
}
