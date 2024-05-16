package me.dueris.genesismc.factory.powers.apoli;

import com.google.gson.JsonObject;
import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.actions.Actions;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.HudRender;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ActionWhenHit extends PowerType implements CooldownPower {
	private final FactoryJsonObject bientityAction;
	private final FactoryJsonObject bientityCondition;
	private final FactoryJsonObject damageCondition;
	private final int cooldown;
	private final HudRender hudRender;

	public ActionWhenHit(String name, String description, boolean hidden, FactoryJsonObject condition,
						 int loading_priority, FactoryJsonObject bientityAction, FactoryJsonObject bientityCondition, FactoryJsonObject damageCondition, int cooldown, FactoryJsonObject hudRender) {
		super(name, description, hidden, condition, loading_priority);
		this.bientityAction = bientityAction;
		this.bientityCondition = bientityCondition;
		this.damageCondition = damageCondition;
		this.cooldown = cooldown;
		this.hudRender = HudRender.createHudRender(hudRender);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("action_when_hit"))
			.add("bientity_action", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("bientity_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("damage_condition", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()))
			.add("cooldown", int.class, 1)
			.add("hud_render", FactoryJsonObject.class, new FactoryJsonObject(new JsonObject()));

	}

	@EventHandler
	public void h(EntityDamageByEntityEvent e) {
		Entity actor = e.getEntity();
		Entity target = e.getDamager();

		if (!(actor instanceof Player player)) return;
		if (!getPlayers().contains(actor) || Cooldown.isInCooldown(player, this)) return;

		if (!isActive(player) || !ConditionExecutor.testBiEntity(bientityCondition, (CraftEntity) actor, (CraftEntity) target) || ConditionExecutor.testDamage(damageCondition, e))
			return;
		Actions.executeBiEntity(actor, target, bientityAction);
		Cooldown.addCooldown(player, cooldown, this);
	}

	@Override
	public HudRender getHudRender() {
		return hudRender;
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

}
