package io.github.dueris.originspaper.condition.type.entity;

import io.github.dueris.originspaper.condition.ConditionConfiguration;
import io.github.dueris.originspaper.condition.type.EntityConditionType;
import io.github.dueris.originspaper.condition.type.EntityConditionTypes;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class GlowingEntityConditionType extends EntityConditionType {

	@Override
	public boolean test(Entity entity) {
		return entity.isCurrentlyGlowing();
	}

	@Override
	public @NotNull ConditionConfiguration<?> getConfig() {
		return EntityConditionTypes.GLOWING;
	}

}