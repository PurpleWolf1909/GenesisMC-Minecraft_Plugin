package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.ExplosionMask;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ExplodeAction {

	public static void action(SerializableData.Instance data, @NotNull Entity entity) {

		Level level = entity.level();
		if (level.isClientSide) {
			return;
		}

		float explosionPower = data.get("power");
		boolean create_fire = data.getBoolean("create_fire");

		Vec3 location = entity.position();

		Explosion explosion = new Explosion(
			level,
			null,
			level.damageSources().generic(),
			new ExplosionDamageCalculator(),
			location.x,
			location.y,
			location.z,
			explosionPower,
			create_fire,
			data.get("destruction_type"),
			ParticleTypes.EXPLOSION,
			ParticleTypes.EXPLOSION_EMITTER,
			SoundEvents.GENERIC_EXPLODE
		);
		ExplosionMask.getExplosionMask(explosion, level).apply(true, data.get("indestructible"), data.get("destructible"), true);

	}

	public static @NotNull ActionFactory<Entity> getFactory() {
		return new ActionFactory<>(
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
