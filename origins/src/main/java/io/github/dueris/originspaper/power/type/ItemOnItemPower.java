package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.factory.ActionTypeFactory;
import io.github.dueris.originspaper.condition.factory.ConditionTypeFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.power.factory.PowerTypeFactory;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

public class ItemOnItemPower extends PowerType {
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> usingItemCondition;
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> onItemCondition;

	private final ActionTypeFactory<Tuple<Level, SlotAccess>> usingItemAction;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> onItemAction;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> resultItemAction;
	private final ActionTypeFactory<Entity> entityAction;

	private final ItemStack resultStack;

	private final int resultFromOnStack;

	public ItemOnItemPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
						   ActionTypeFactory<Entity> entityAction, ActionTypeFactory<Tuple<Level, SlotAccess>> usingItemAction, ActionTypeFactory<Tuple<Level, SlotAccess>> onItemAction, ActionTypeFactory<Tuple<Level, SlotAccess>> resultItemAction,
						   ConditionTypeFactory<Tuple<Level, ItemStack>> usingItemCondition, ConditionTypeFactory<Tuple<Level, ItemStack>> onItemCondition, ItemStack resultStack, int resultFromOnStack) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.usingItemCondition = usingItemCondition;
		this.onItemCondition = onItemCondition;
		this.resultStack = resultStack;
		this.usingItemAction = usingItemAction;
		this.onItemAction = onItemAction;
		this.resultItemAction = resultItemAction;
		this.entityAction = entityAction;
		this.resultFromOnStack = resultFromOnStack;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("item_on_item"), PowerType.getFactory().getSerializableData()
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("using_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("on_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("using_item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("on_item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("result", SerializableDataTypes.ITEM_STACK, null)
			.add("result_from_on_stack", SerializableDataTypes.INT, 0));
	}

	public boolean doesApply(ItemStack usingStack, ItemStack onStack, Entity entity) {
		return (onItemCondition == null || onItemCondition.test(new Tuple<>(entity.level(), onStack)))
			&& (usingItemCondition == null || usingItemCondition.test(new Tuple<>(entity.level(), usingStack)));
	}

	public void execute(SlotAccess usingStackRef, SlotAccess onStackRef, Slot slot, Entity entity) {

		SlotAccess resultStackRef = Util.createStackReference(resultStack != null
			? resultStack.copy()
			: resultFromOnStack > 0
			? onStackRef.get().split(resultFromOnStack)
			: onStackRef.get());

		if (resultItemAction != null) {
			resultItemAction.accept(new Tuple<>(entity.level(), resultStackRef));
		}

		if (usingItemAction != null) {
			usingItemAction.accept(new Tuple<>(entity.level(), usingStackRef));
		}

		if (onItemAction != null) {
			onItemAction.accept(new Tuple<>(entity.level(), onStackRef));
		}

		if (entity instanceof Player player && (resultStack != null || resultItemAction != null)) {

			if (slot.hasItem()) {
				player.getInventory().placeItemBackInInventory(resultStackRef.get());
			} else {
				slot.set(resultStackRef.get());
			}

		}

		if (entityAction != null) {
			entityAction.accept(entity);
		}

	}

	@EventHandler
	public void itemOnItem(@NotNull InventoryClickEvent e) {
		if (e.getCurrentItem() != null && (e.getCursor() != null && !e.getCursor().getType().isAir())) {
			org.bukkit.entity.Player p = (org.bukkit.entity.Player) e.getWhoClicked();
			Player player = ((CraftPlayer) p).getHandle();
			if (this.getPlayers().contains(player)) {
				ItemStack usingItem = CraftItemStack.unwrap(e.getCursor());
				ItemStack onItem = CraftItemStack.unwrap(e.getCurrentItem());
				if (doesApply(
					usingItem, onItem, player
				)) {
					execute(Util.createStackReference(usingItem),
						Util.createStackReference(onItem), ((CraftInventoryView) e.getView()).getHandle().getSlot(e.getSlot()), player);
				}
			}
		}
	}
}