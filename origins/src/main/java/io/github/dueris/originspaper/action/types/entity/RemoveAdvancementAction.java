package io.github.dueris.originspaper.action.types.entity;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.world.entity.Entity;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class RemoveAdvancementAction {

	public static void action(@NotNull SerializableData.Instance data, @NotNull Entity entity) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke $1 only $2"
			.replace("$1", entity.getName().getString())
			.replace("$2", data.getString("advancement")));
	}

	public static @NotNull ActionTypeFactory<Entity> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("revoke_advancement"),
			SerializableData.serializableData()
				.add("advancement", SerializableDataTypes.IDENTIFIER, null),
			RemoveAdvancementAction::action
		);
	}
}
