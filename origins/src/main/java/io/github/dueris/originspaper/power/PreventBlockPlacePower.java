package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionFactory;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.craftbukkit.CraftEquipmentSlot;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class PreventBlockPlacePower extends PowerType {
	private final ActionFactory<Entity> entityAction;
	private final ActionFactory<Triple<Level, BlockPos, Direction>> placeToAction;
	private final ActionFactory<Triple<Level, BlockPos, Direction>> placeOnAction;
	private final ConditionFactory<Tuple<Level, ItemStack>> itemCondition;
	private final ConditionFactory<BlockInWorld> placeToCondition;
	private final ConditionFactory<BlockInWorld> placeOnCondition;
	private final EnumSet<Direction> directions;
	private final EnumSet<InteractionHand> hands;
	private final ItemStack resultStack;
	private final ActionFactory<Tuple<Level, SlotAccess>> resultItemAction;
	private final ActionFactory<Tuple<Level, SlotAccess>> heldItemAction;

	public PreventBlockPlacePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
								  ActionFactory<Entity> entityAction, ActionFactory<Triple<Level, BlockPos, Direction>> placeToAction, ActionFactory<Triple<Level, BlockPos, Direction>> placeOnAction,
								  ConditionFactory<Tuple<Level, ItemStack>> itemCondition, ConditionFactory<BlockInWorld> placeToCondition, ConditionFactory<BlockInWorld> placeOnCondition, EnumSet<Direction> directions,
								  EnumSet<InteractionHand> hands, ItemStack resultStack, ActionFactory<Tuple<Level, SlotAccess>> resultItemAction, ActionFactory<Tuple<Level, SlotAccess>> heldItemAction) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.entityAction = entityAction;
		this.placeToAction = placeToAction;
		this.placeOnAction = placeOnAction;
		this.itemCondition = itemCondition;
		this.placeToCondition = placeToCondition;
		this.placeOnCondition = placeOnCondition;
		this.directions = directions;
		this.hands = hands;
		this.resultStack = resultStack;
		this.resultItemAction = resultItemAction;
		this.heldItemAction = heldItemAction;
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

	public void executeActions(Entity entity, InteractionHand hand, BlockPos toPos, BlockPos onPos, Direction direction) {

		if (placeOnAction != null) {
			placeOnAction.accept(Triple.of(entity.level(), onPos, direction));
		}

		if (placeToAction != null) {
			placeToAction.accept(Triple.of(entity.level(), toPos, direction));
		}

		if (entityAction != null) {
			entityAction.accept(entity);
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

	public boolean doesPrevent(@NotNull Entity entity, ItemStack heldStack, InteractionHand hand, BlockPos toPos, BlockPos onPos, Direction direction) {
		return (shouldExecute$apoli$super(hand, heldStack, (ServerLevel) entity.level()) && directions.contains(direction))
			&& ((placeOnCondition == null || placeOnCondition.test(new BlockInWorld(entity.level(), onPos, true)))
			&& (placeToCondition == null || placeToCondition.test(new BlockInWorld(entity.level(), toPos, true))));
	}

	@EventHandler
	public void onBlockPlace(@NotNull BlockPlaceEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player) && isActive(player)) {
			ItemStack held = CraftItemStack.unwrap(e.getItemInHand());
			InteractionHand hand = CraftEquipmentSlot.getHand(e.getHand());
			BlockPos toPos = ((CraftBlock)e.getBlock()).getPosition();
			BlockPos onPos = ((CraftBlock)e.getBlockAgainst()).getPosition();
			Direction direction = CraftBlock.blockFaceToNotch(e.getBlockAgainst().getFace(e.getBlockPlaced()));
			if (doesPrevent(player, held,  hand, toPos, onPos, direction)) {
				executeActions(player, hand, toPos, onPos, direction);
				e.setCancelled(true);
			}
		}
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("prevent_block_place"))
			.add("entity_action", ApoliDataTypes.ENTITY_ACTION, null)
			.add("place_to_action", ApoliDataTypes.BLOCK_ACTION, null)
			.add("place_on_action", ApoliDataTypes.BLOCK_ACTION, null)
			.add("item_condition", ApoliDataTypes.ITEM_CONDITION, null)
			.add("place_to_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("place_on_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("directions", SerializableDataTypes.DIRECTION_SET, EnumSet.allOf(Direction.class))
			.add("hands", SerializableDataTypes.enumSet(InteractionHand.class, SerializableDataTypes.HAND), EnumSet.allOf(InteractionHand.class))
			.add("result_stack", SerializableDataTypes.ITEM_STACK, null)
			.add("result_item_action", ApoliDataTypes.ITEM_ACTION, null)
			.add("held_item_action", ApoliDataTypes.ITEM_ACTION, null);
	}
}