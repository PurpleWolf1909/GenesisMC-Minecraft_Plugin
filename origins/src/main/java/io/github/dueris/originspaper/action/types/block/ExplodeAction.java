package io.github.dueris.originspaper.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.ExplosionMask;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class ExplodeAction {

	public static void action(SerializableData.Instance data, @NotNull Triple<Level, BlockPos, Direction> block) {

		Level level = block.getLeft();
		if (level.isClientSide) {
			return;
		}

		float explosionPower = data.get("power");
		boolean create_fire = data.getBoolean("create_fire");

		BlockPos location = block.getMiddle();

		Explosion explosion = new Explosion(
			level,
			null,
			level.damageSources().generic(),
			new ExplosionDamageCalculator(),
			location.getX(),
			location.getY(),
			location.getZ(),
			explosionPower,
			create_fire,
			data.get("destruction_type"),
			ParticleTypes.EXPLOSION,
			ParticleTypes.EXPLOSION_EMITTER,
			SoundEvents.GENERIC_EXPLODE
		);
		ExplosionMask.getExplosionMask(explosion, level).apply(true, data.get("indestructible"), data.get("destructible"), true);

	}

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("explode"),
			SerializableData.serializableData()
				.add("power", SerializableDataTypes.FLOAT)
				.add("destruction_type", ApoliDataTypes.BACKWARDS_COMPATIBLE_DESTRUCTION_TYPE, Explosion.BlockInteraction.DESTROY)
				.add("indestructible", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("indestructible_resistance", SerializableDataTypes.FLOAT, 10.0f)
				.add("destructible", ApoliDataTypes.BLOCK_CONDITION, null)
				.add("create_fire", SerializableDataTypes.BOOLEAN, false),
			ExplodeAction::action
		);
	}
}
