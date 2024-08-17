package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.data.types.Shape;
import io.github.dueris.originspaper.util.entity.GlowingEntitiesUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Set;

import static io.github.dueris.originspaper.power.EntityGlowPower.translateBarColor;

public class SelfGlowPower extends PowerType {
	private final ConditionFactory<Entity> entityCondition;
	private final ConditionFactory<Tuple<Entity, Entity>> bientityCondition;
	private final boolean useTeams;
	private final float r;
	private final float g;
	private final float b;

	public SelfGlowPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
						 ConditionFactory<Entity> entityCondition, ConditionFactory<Tuple<Entity, Entity>> bientityCondition, boolean useTeams, float r, float g, float b) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityCondition = entityCondition;
		this.bientityCondition = bientityCondition;
		this.useTeams = useTeams;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("self_glow"))
			.add("entity_condition", ApoliDataTypes.ENTITY_CONDITION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("use_teams", SerializableDataTypes.BOOLEAN, true)
			.add("red", SerializableDataTypes.FLOAT, 1.0F)
			.add("green", SerializableDataTypes.FLOAT, 1.0F)
			.add("blue", SerializableDataTypes.FLOAT, 1.0F);
	}

	@Override
	public void tick(@NotNull Player player) {
		ServerLevel level = (ServerLevel) player.level();
		Set<Entity> entities = Shape.getEntities(Shape.SPHERE, level, player.position(), 60);
		entities.add(player);
		GlowingEntitiesUtils utils = OriginsPaper.glowingEntitiesUtils;
		try {
			if (isActive(player)) {
				for (Entity entity : entities) {
					if (!(entity instanceof ServerPlayer receiver)) continue;
					if (entityCondition != null && !entityCondition.test(entity)) {
						utils.unsetGlowing(player.getBukkitEntity(), receiver.getBukkitEntity());
						continue;
					}
					if (bientityCondition != null && !bientityCondition.test(new Tuple<>(player, entity))) {
						utils.unsetGlowing(player.getBukkitEntity(), receiver.getBukkitEntity());
						continue;
					}
					if (useTeams && player.getTeam() != null) {
						utils.setGlowing(player.getBukkitEntity(), receiver.getBukkitEntity(), CraftChatMessage.getColor(player.getTeam().getColor()));
					} else {
						java.awt.Color awtColor = new Color(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
						utils.setGlowing(player.getBukkitEntity(), receiver.getBukkitEntity(), translateBarColor(EntityGlowPower.GlowTranslator.getColor(awtColor)));
					}
				}
			} else {
				for (Entity entity : entities) {
					if (!(entity instanceof ServerPlayer receiver)) continue;
					utils.unsetGlowing(player.getBukkitEntity(), receiver.getBukkitEntity());
				}
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}