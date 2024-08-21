package io.github.dueris.originspaper.condition.types.bientity;

import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class RidingRecursiveCondition {

	public static boolean condition(SerializableData.Instance data, @NotNull Tuple<Entity, Entity> actorAndTarget) {

		Entity actor = actorAndTarget.getA();
		Entity target = actorAndTarget.getB();

		if ((actor == null || target == null) || !actor.isPassenger()) {
			return false;
		}

		Entity vehicle = actor.getVehicle();
		while (vehicle != null && !vehicle.equals(target)) {
			vehicle = vehicle.getVehicle();
		}

		return target.equals(vehicle);

	}

	public static @NotNull ConditionFactory<Tuple<Entity, Entity>> getFactory() {
		return new ConditionFactory<>(
			OriginsPaper.apoliIdentifier("riding_recursive"),
			SerializableData.serializableData(),
			RidingRecursiveCondition::condition
		);
	}
}
