package me.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.condition.ConditionFactory;
import me.dueris.originspaper.registry.registries.PowerType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class ExhaustOverTimePower extends PowerType {
	private final int exhaustInterval;
	private final float exhaustion;

	public ExhaustOverTimePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								int exhaustInterval, float exhaustion) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.exhaustInterval = exhaustInterval;
		this.exhaustion = exhaustion;
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("exhaust"))
			.add("interval", SerializableDataTypes.POSITIVE_INT, 20)
			.add("exhaustion", SerializableDataTypes.FLOAT);
	}

	@Override
	public void tick(@NotNull Player player) {
		if (player.tickCount % exhaustInterval == 0 && isActive(player)) {
			player.causeFoodExhaustion(exhaustion);
		}
	}
}
