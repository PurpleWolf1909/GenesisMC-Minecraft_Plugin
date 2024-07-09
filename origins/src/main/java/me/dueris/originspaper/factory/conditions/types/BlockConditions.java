package me.dueris.originspaper.factory.conditions.types;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.conditions.ConditionExecutor;
import me.dueris.originspaper.factory.data.types.Comparison;
import me.dueris.originspaper.factory.data.types.Shape;
import me.dueris.originspaper.factory.data.types.VectorGetter;
import me.dueris.originspaper.registry.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.TileState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiPredicate;

public class BlockConditions {

	public void registerConditions() {
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("material"), (condition, block) -> {
			Material mat = condition.getMaterial(condition.getString("material"));
			return block.getType().equals(mat);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("in_tag"), (condition, block) -> {
			if (block == null || block.getNMS() == null) return false;
			NamespacedKey tag = NamespacedKey.fromString(condition.getString("tag"));
			TagKey<Block> key = TagKey.create(net.minecraft.core.registries.Registries.BLOCK, CraftNamespacedKey.toMinecraft(tag));
			return block.getHandle().getBlockState(CraftLocation.toBlockPosition(block.getLocation())).is(key);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("adjacent"), (condition, block) -> {
			int adj = 0;
			for (Direction direction : Direction.values()) {
				boolean p = true;
				if (condition.isPresent("adjacent_condition")) {
					p = ConditionExecutor.testBlock(condition.getJsonObject("adjacent_condition"), (CraftBlock) block.getWorld().getBlockAt(CraftLocation.toBukkit(block.getPosition().offset(direction.getNormal()))));
				}
				if (p) {
					adj++;
				}
			}
			String comparison = condition.getString("comparison");
			float compare_to = condition.getNumber("compare_to").getFloat();

			return Comparison.fromString(comparison).compare(adj, compare_to);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("attachable"), (condition, block) -> {
			ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
			for (Direction d : Direction.values()) {
				BlockPos adjacent = CraftLocation.toBlockPosition(block.getLocation()).relative(d);
				if (level.getBlockState(adjacent).isFaceSturdy(level, CraftLocation.toBlockPosition(block.getLocation()), d.getOpposite())) {
					return true;
				}
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("blast_resistance"), (condition, block) -> {
			String comparison = condition.getString("comparison");
			float compare_to = condition.getNumber("compare_to").getFloat();
			float bR = block.getType().getBlastResistance();
			return Comparison.fromString(comparison).compare(bR, compare_to);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("block_entity"), (condition, block) -> block.getState() instanceof TileState));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("block"), (condition, block) -> {
			String r = condition.getString("block");
			Block bl = BuiltInRegistries.BLOCK.get(CraftNamespacedKey.toMinecraft(NamespacedKey.fromString(r)));
			return block.getNMS().is(bl);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("distance_from_coordinates"), (condition, block) -> {
			boolean scaleReferenceToDimension = condition.getBooleanOrDefault("scale_reference_to_dimension", true);
			boolean setResultOnWrongDimension = condition.isPresent("result_on_wrong_dimension"), resultOnWrongDimension = setResultOnWrongDimension && condition.getBoolean("result_on_wrong_dimension");
			double x = 0, y = 0, z = 0;
			Vec3 pos = CraftLocation.toVec3D(block.getLocation());
			ServerLevel level = block.getHandle().getMinecraftWorld();

			double currentDimensionCoordinateScale = level.dimensionType().coordinateScale();
			switch (condition.getStringOrDefault("reference", "world_origin")) {
				case "player_natural_spawn", "world_spawn", "player_spawn":
					if (setResultOnWrongDimension && level.dimension() != Level.OVERWORLD)
						return resultOnWrongDimension;
					BlockPos spawnPos = level.getSharedSpawnPos();
					x = spawnPos.getX();
					y = spawnPos.getY();
					z = spawnPos.getZ();
					break;
				case "world_origin":
					break;
			}

			Gson gson = new Gson();
			Map<String, Integer> fallbackMapConstant = Map.of("x", 0, "y", 0, "z", 0);
			FactoryJsonObject jsonObjectFallback = new FactoryJsonObject(gson.fromJson(gson.toJson(fallbackMapConstant), JsonObject.class));
			Vec3 coords = VectorGetter.getNMSVector(condition.isPresent("coordinates") ? condition.getJsonObject("coordinates") : jsonObjectFallback);
			Vec3 offset = VectorGetter.getNMSVector(condition.isPresent("offset") ? condition.getJsonObject("offset") : jsonObjectFallback);
			x += coords.x + offset.x;
			y += coords.y + offset.y;
			z += coords.z + offset.z;
			if (scaleReferenceToDimension && (x != 0 || z != 0)) {
				Comparison comparison = Comparison.fromString(condition.getString("comparison"));
				if (currentDimensionCoordinateScale == 0)
					return comparison == Comparison.NOT_EQUAL || comparison == Comparison.GREATER_THAN || comparison == Comparison.GREATER_THAN_OR_EQUAL;

				x /= currentDimensionCoordinateScale;
				z /= currentDimensionCoordinateScale;
			}

			double distance,
				xDistance = condition.getBooleanOrDefault("ignore_x", false) ? 0 : Math.abs(pos.x() - x),
				yDistance = condition.getBooleanOrDefault("ignore_y", false) ? 0 : Math.abs(pos.y() - y),
				zDistance = condition.getBooleanOrDefault("ignore_z", false) ? 0 : Math.abs(pos.z() - z);
			if (condition.getBooleanOrDefault("scale_distance_to_dimension", false)) {
				xDistance *= currentDimensionCoordinateScale;
				zDistance *= currentDimensionCoordinateScale;
			}

			distance = Shape.getDistance(condition.getEnumValueOrDefault("shape", Shape.class, Shape.CUBE), xDistance, yDistance, zDistance);

			if (condition.isPresent("round_to_digit")) {
				distance = new BigDecimal(distance).setScale(condition.getNumber("round_to_digit").getInt(), RoundingMode.HALF_UP).doubleValue();
			}

			return Comparison.fromString(condition.getString("comparison")).compare(distance, condition.getNumber("compare_to").getFloat());
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("block_state"), (condition, block) -> {
			BlockState state = block.getNMS();
			Collection<Property<?>> properties = state.getProperties();
			String desiredPropertyName = condition.getString("property");
			Property<?> property = null;
			for (Property<?> p : properties) {
				if (p.getName().equals(desiredPropertyName)) {
					property = p;
					break;
				}
			}
			if (property != null) {
				Object value = state.getValue(property);
				if (condition.isPresent("enum") && value instanceof Enum) {
					return ((Enum) value).name().equalsIgnoreCase(condition.getString("enum"));
				} else if (condition.isPresent("value") && value instanceof Boolean) {
					return ((boolean) value) == condition.getElement("value").getBoolean();
				} else if (condition.isPresent("comparison") && condition.isPresent("compare_to") && value instanceof Integer valInt) {
					return Comparison.fromString(condition.getString("comparison")).compare(valInt, condition.getNumber("compare_to").getInt());
				}
				return true;
			}
			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("exposed_to_sky"), (condition, block) -> block.getLightFromSky() > 0));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("fluid"), (condition, block) -> ConditionExecutor.testFluid(condition.getJsonObject("fluid_condition"), block.getHandle().getFluidState(new BlockPos(block.getX(), block.getY(), block.getZ())).getType())));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("hardness"), (condition, block) -> {
			String comparison = condition.getString("comparison");
			float compare_to = condition.getNumber("compare_to").getFloat();
			float bR = block.getType().getHardness();
			return Comparison.fromString(comparison).compare(bR, compare_to);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("height"), (condition, block) -> {
			String comparison = condition.getString("comparison");
			float compare_to = condition.getNumber("compare_to").getFloat();
			float bR = block.getLocation().getBlockY();
			return Comparison.fromString(comparison).compare(bR, compare_to);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("light_blocking"), (condition, block) -> !block.getType().isOccluding()));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("light_level"), (condition, block) -> {
			ServerLevel level = block.getHandle().getMinecraftWorld();
			BlockPos pos = block.getPosition();

			int lightLevel;

			if (condition.isPresent("light_type")) {
				lightLevel = level.getBrightness(condition.getEnumValue("light_type", LightLayer.class), pos);
			} else {
				lightLevel = level.getMaxLocalRawBrightness(pos);
			}
			String comparison = condition.getString("comparison");
			float compare_to = condition.getNumber("compare_to").getFloat();
			return Comparison.fromString(comparison).compare(lightLevel, compare_to);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("slipperiness"), (condition, block) -> {
			String comparison = condition.getString("comparison");
			float compare_to = condition.getNumber("compare_to").getFloat();
			return Comparison.fromString(comparison).compare(block.getBlockData().getMaterial().getSlipperiness(), compare_to);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("piston_behavior"), (condition, block) -> block.getNMS().getPistonPushReaction().equals(condition.getEnumValue("behavior", PushReaction.class))));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("redstone_input"), (condition, block) -> {
			Comparison comparison = Comparison.fromString(condition.getString("comparison"));
			int compareTo = condition.getNumber("compare_to").getInt();
			int receivedRedstonePower = ((CraftWorld) block.getWorld()).getHandle().getBestNeighborSignal(CraftLocation.toBlockPosition(block.getLocation()));
			return comparison.compare(receivedRedstonePower, compareTo);
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("redstone_output"), (condition, block) -> {
			Comparison comparison = Comparison.fromString(condition.getString("comparison"));
			int compareTo = condition.getNumber("compare_to").getInt();
			ServerLevel level = ((CraftWorld) block.getWorld()).getHandle();
			BlockPos cachedBlockPos = CraftLocation.toBlockPosition(block.getLocation());

			for (Direction direction : Direction.values()) {
				int emittedRedstonePower = level.getSignal(cachedBlockPos, direction);
				if (comparison.compare(emittedRedstonePower, compareTo)) {
					return true;
				}
			}

			return false;
		}));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("requires_tool"), (condition, block) -> block.getNMS().requiresCorrectToolForDrops()));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("movement_blocking"), (condition, block) -> block.getType().isCollidable()));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("replacable"), (condition, block) -> block.getType().isAir() || block.isReplaceable()));
		register(new ConditionFactory(OriginsPaper.apoliIdentifier("water_loggable"), (condition, block) -> block.getHandle().getBlockState(block.getPosition()).getBlock() instanceof LiquidBlockContainer));
	}

	public void register(ConditionFactory factory) {
		OriginsPaper.getPlugin().registry.retrieve(Registries.BLOCK_CONDITION).register(factory);
	}

	public class ConditionFactory implements Registrable {
		NamespacedKey key;
		BiPredicate<FactoryJsonObject, CraftBlock> test;

		public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, CraftBlock> test) {
			this.key = key;
			this.test = test;
		}

		public boolean test(FactoryJsonObject condition, CraftBlock tester) {
			return test.test(condition, tester);
		}

		@Override
		public NamespacedKey key() {
			return key;
		}
	}
}