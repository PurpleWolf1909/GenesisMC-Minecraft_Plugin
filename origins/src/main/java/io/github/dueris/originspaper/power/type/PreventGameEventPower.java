package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.data.SerializableDataType;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class PreventGameEventPower extends PowerType {
	private final LinkedList<Holder<GameEvent>> events;
	private final ActionTypeFactory<Entity> entityAction;
	private final TagKey<GameEvent> eventTag;

	public PreventGameEventPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								 Holder<GameEvent> event, List<Holder<GameEvent>> events, TagKey<GameEvent> eventTag, ActionTypeFactory<Entity> entityAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);

		this.events = new LinkedList<>();
		this.eventTag = eventTag;
		this.entityAction = entityAction;

		if (event != null) {
			this.events.add(event);
		}

		if (events != null) {
			this.events.addAll(events);
		}
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("prevent_game_event"), PowerType.getFactory().getSerializableData()
			.add("event", SerializableDataTypes.GAME_EVENT_ENTRY, null)
			.add("events", SerializableDataType.of(SerializableDataTypes.GAME_EVENT_ENTRY.listOf()), null)
			.add("tag", SerializableDataTypes.GAME_EVENT_TAG, null)
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null));
	}

	public void executeAction(Entity entity) {
		if (entityAction != null) {
			entityAction.accept(entity);
		}
	}

	public boolean doesPrevent(Holder<GameEvent> event) {
		return (eventTag != null && event.is(eventTag))
			|| (events != null && events.contains(event));
	}
}