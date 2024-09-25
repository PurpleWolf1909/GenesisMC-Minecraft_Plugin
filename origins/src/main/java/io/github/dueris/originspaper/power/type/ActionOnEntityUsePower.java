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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.SlotRanges;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class ActionOnEntityUsePower extends PowerType {
	private final ActionTypeFactory<Tuple<Entity, Entity>> biEntityAction;
	private final ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition;
	private final ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition;
	private final EnumSet<InteractionHand> hands;
	private final ItemStack resultStack;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> heldItemAction;
	private final ActionTypeFactory<Tuple<Level, SlotAccess>> resultItemAction;

	public ActionOnEntityUsePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionTypeFactory<Entity> condition, int loadingPriority,
								  ActionTypeFactory<Tuple<Entity, Entity>> bientityAction, ConditionTypeFactory<Tuple<Entity, Entity>> bientityCondition, ConditionTypeFactory<Tuple<Level, ItemStack>> itemCondition,
								  EnumSet<InteractionHand> hands, ItemStack resultStack, ActionTypeFactory<Tuple<Level, SlotAccess>> heldItemAction, ActionTypeFactory<Tuple<Level, SlotAccess>> resultItemAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.biEntityAction = bientityAction;
		this.bientityCondition = bientityCondition;
		this.itemCondition = itemCondition;
		this.hands = hands;
		this.resultStack = resultStack;
		this.heldItemAction = heldItemAction;
		this.resultItemAction = resultItemAction;
	}

	public static @NotNull PowerTypeFactory getFactory() {
		return new PowerTypeFactory(OriginsPaper.apoliIdentifier("action_on_entity_use"), PowerType.getFactory().getSerializableData()
			.add("bientity_action", ApoliDataTypes.BIENTITY_ACTION, null)
			.add("bientity_condition", ApoliDataTypes.BIENTITY_CONDITION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("hands", SerializableDataTypes.enumSet(InteractionHand.class, SerializableDataTypes.HAND), EnumSet.allOf(InteractionHand.class))
			.add("result_stack", SerializableDataTypes.ITEM_STACK, null)
			.add("held_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null));
	}

	public boolean shouldExecute$apoli$super(InteractionHand hand, ItemStack heldStack, ServerLevel level) {
		if (!doesApplyToHand(hand)) {
			return false;
		}
		return doesApplyToItem(heldStack, level);
	}

	public boolean doesApplyToHand(InteractionHand hand) {
		return hands.contains(hand);
	}

	public boolean doesApplyToItem(ItemStack heldStack, ServerLevel level) {
		return itemCondition == null || itemCondition.test(new Tuple<>(level, heldStack));
	}

	public boolean shouldExecute(@NotNull Entity other, InteractionHand hand, ItemStack heldStack, Entity entity) {
		return shouldExecute$apoli$super(hand, heldStack, (ServerLevel) other.level())
			&& (bientityCondition == null || bientityCondition.test(new Tuple<>(entity, other)))
			&& isActive(entity);
	}

	public void executeAction(Entity target, InteractionHand hand, Entity entity) {

		if (biEntityAction != null) {
			biEntityAction.accept(new Tuple<>(entity, target));
		}

		if (entity instanceof Player other) {
			SlotAccess heldStack = other.getSlot(hand == InteractionHand.OFF_HAND ? SlotRanges.nameToIds("weapon.offhand").slots().getFirst() : SlotRanges.nameToIds("weapon.mainhand").slots().getFirst());
			if (heldItemAction != null) {
				heldItemAction.accept(new Tuple<>(other.level(), heldStack));
			}
			if (resultStack != null) {
				heldStack.set(resultStack);
			}
			SlotAccess resultingStack = Util.createStackReference(resultStack == null ? heldStack.get() : resultStack.copy());
			boolean modified = resultStack != null;
			if (resultItemAction != null) {
				resultItemAction.accept(new Tuple<>(other.level(), heldStack));
				modified = true;
			}
			if (modified) {
				if (heldStack.get().isEmpty()) {
					other.setItemInHand(hand, resultingStack.get());
				} else {
					other.getInventory().placeItemBackInInventory(resultingStack.get());
				}
			}
		}

	}

	@EventHandler
	public void onEntityUse(@NotNull PlayerInteractAtEntityEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (isActive(player)) {
			Entity target = ((CraftEntity) e.getRightClicked()).getHandle();
			InteractionHand hand = CraftEquipmentSlot.getHand(e.getHand());
			ItemStack stack = e.getPlayer().getInventory().getItem(e.getHand()) != null ? player.getItemInHand(hand) : ItemStack.EMPTY;
			if (shouldExecute(target, hand, stack, player)) {
				executeAction(target, hand, player);
			}
		}
	}
}