package io.github.dueris.originspaper.power;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.ConditionFactory;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.util.ApoliScheduler;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModifyHarvestPower extends PowerType {
	private final static Set<String> ALREADY_TICKED = new ConcurrentSet<>();
	private final ConditionFactory<BlockInWorld> blockCondition;
	private final boolean allow;

	public ModifyHarvestPower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							  ConditionFactory<BlockInWorld> blockCondition, boolean allow) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.blockCondition = blockCondition;
		this.allow = allow;
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(OriginsPaper.apoliIdentifier("modify_harvest"))
			.add("block_condition", ApoliDataTypes.BLOCK_CONDITION, null)
			.add("allow", SerializableDataTypes.BOOLEAN);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBreak(@NotNull BlockBreakEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		BlockPos pos = CraftLocation.toBlockPosition(e.getBlock().getLocation());
		if (getPlayers().contains(player) && isActive(player) && doesApply(pos, player) && isHarvestAllowed() && !e.isCancelled() && !ALREADY_TICKED.contains(getTag())) {
			boolean willDrop = player.hasCorrectToolForDrops(((CraftBlock) e.getBlock()).getNMS());
			if (allow && !willDrop) {
				ALREADY_TICKED.add(getTag());
				e.getBlock().getDrops().forEach((itemStack -> {
					player.level().getWorld().dropItemNaturally(e.getBlock().getLocation(), itemStack);
				}));
				ApoliScheduler.INSTANCE.queue((m) -> {
					ALREADY_TICKED.remove(getTag());
				}, 1);
			}

		}
	}

	public boolean doesApply(BlockPos pos, @NotNull Entity entity) {
		return doesApply(new BlockInWorld(entity.level(), pos, true));
	}

	public boolean doesApply(BlockInWorld pos) {
		return blockCondition == null || blockCondition.test(pos);
	}

	public boolean isHarvestAllowed() {
		return allow;
	}
}
