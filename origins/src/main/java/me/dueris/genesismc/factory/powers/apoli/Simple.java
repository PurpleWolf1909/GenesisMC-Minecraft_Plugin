package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;

public class Simple extends PowerType {

	@Register
	public Simple(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data)
			.ofNamespace(GenesisMC.apoliIdentifier("simple"));
	}

}
