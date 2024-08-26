package io.github.dueris.originspaper.action.types.block;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.ActionTypeFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

public class BoneMealAction {

	public static void action(@NotNull SerializableData.Instance data, @NotNull Triple<Level, BlockPos, Direction> block) {
		Level world = block.getLeft();
		BlockPos blockPos = block.getMiddle();
		Direction side = block.getRight();
		BlockPos blockPos2 = blockPos.relative(side);

		boolean spawnEffects = data.getBoolean("effects");

		if (BoneMealItem.growCrop(ItemStack.EMPTY, world, blockPos)) {
			if (spawnEffects && !world.isClientSide) {
				world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos, 0);
			}
		} else {
			BlockState blockState = world.getBlockState(blockPos);
			boolean bl = blockState.isFaceSturdy(world, blockPos, side);
			if (bl && BoneMealItem.growWaterPlant(ItemStack.EMPTY, world, blockPos2, side)) {
				if (spawnEffects && !world.isClientSide) {
					world.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, blockPos2, 0);
				}
			}
		}
	}

	public static @NotNull ActionTypeFactory<Triple<Level, BlockPos, Direction>> getFactory() {
		return new ActionTypeFactory<>(
			OriginsPaper.apoliIdentifier("bonemeal"),
			SerializableData.serializableData()
				.add("effects", SerializableDataTypes.BOOLEAN, true),
			BoneMealAction::action
		);
	}
}
