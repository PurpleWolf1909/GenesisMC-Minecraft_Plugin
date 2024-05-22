package me.dueris.genesismc.factory.data.types;

import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.util.Util;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public class Modifier {
	public FactoryJsonObject handle;

	public Modifier(FactoryJsonObject factoryJsonObject) {
		this.handle = factoryJsonObject;
	}

	public static Modifier[] getModifiers(@Nullable FactoryJsonObject singular, @Nullable FactoryJsonArray plural) {
		List<Modifier> modifiers = new ArrayList<>();
		if (singular != null) modifiers.add(new Modifier(singular));
		if (plural != null && !plural.asList().isEmpty())
			modifiers.addAll(plural.asJsonObjectList().stream().map(Modifier::new).toList());
		return modifiers.toArray(new Modifier[0]);
	}

	public Float value() {
		if (handle.isPresent("modifier")) {
			Modifier modifier = new Modifier(handle.getJsonObject("modifier"));
			Map<String, BinaryOperator<Float>> operators = Util.getOperationMappingsFloat();
			BinaryOperator<Float> operation = operators.get(modifier.operation());
			return operation.apply(this.value(), modifier.value());
		} else {
			return this.handle.getNumber("value").getFloat();
		}
	}

	public String operation() {
		return this.handle.getStringOrDefault("operation", "add");
	}
}
