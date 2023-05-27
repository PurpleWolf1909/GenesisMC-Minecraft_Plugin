package me.dueris.genesismc.core.factory.powers.entity;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.dueris.genesismc.core.api.entity.OriginPlayer;
import org.bukkit.FluidCollisionMode;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static me.dueris.genesismc.core.factory.powers.Powers.extra_reach;

public class Reach implements Listener {

    @EventHandler
    public void OnClickREACH(PlayerInteractEvent e) {
        if (extra_reach.contains(OriginPlayer.getOriginTag(e.getPlayer()))) {
            if (e.getAction().isLeftClick());

            Player p = e.getPlayer();
            Location eyeloc = p.getEyeLocation();
            @NotNull Vector direction = eyeloc.getDirection();
            Predicate<Entity> filter = (entity) -> !entity.equals(p);

            RayTraceResult traceResult4_5F = p.getWorld().rayTrace(eyeloc, eyeloc.getDirection(), 6, FluidCollisionMode.NEVER, false, 0, filter);

            if (traceResult4_5F != null) {
                Entity entity = traceResult4_5F.getHitEntity();
                //entity code -- pvp
                if (entity == null) return;
                Player attacker = (Player) e.getPlayer();
                if(entity.isDead() || !(entity instanceof LivingEntity)) return;
                LivingEntity victim = (LivingEntity) traceResult4_5F.getHitEntity();
                if(attacker.getLocation().distance(victim.getLocation()) <=6){
                    if (entity.getPassengers().contains(p)) return;
                    if (!entity.isDead()) {
                        LivingEntity ent = (LivingEntity) entity;
                        p.attack(ent);
                    }
                }
            }
        }
    }
    /*

    @EventHandler
    public void SwingBlockBreakCreative(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(p.getGameMode() != GameMode.CREATIVE) return;
        if(e.getAction().isLeftClick()) {
            if (getClosestBlockInSight(p, 3, 6) == null) return;
            getClosestBlockInSight(p, 3, 6).breakNaturally(false, false);
        }
    }

    @EventHandler
    public void BlockPlace(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(p.getGameMode() == GameMode.SPECTATOR) return;
        if(e.getAction().isRightClick()) {
            if (getClosestBlockInSight(p, 3, 6) == null) return;
            if (p.getInventory().getItemInMainHand() == null) return;
            if (p.getInventory().getItemInMainHand().getType().isBlock()) {
                placeBlockInSight(p, 3, 6);
            }
        }
    }
    */

    @EventHandler
    public void SwingBlockBreakSurvival(PlayerArmSwingEvent e){
        Player p = e.getPlayer();
        if(getClosestBlockInSight(p, 3, 6) == null) return;
    }
        private int calculateBreakingTime(Material blockType) {
            return 20; // Default value of 20 ticks (1 second)
        }

        private int calculateToolEfficiency(ItemStack tool) {
            ItemStack item = tool;

            if (item == null || item.getType() == Material.AIR) {
                return 1;
            }

            Material itemType = item.getType();
            double miningSpeed = 1; // Default mining speed

            // Check if the item has the Efficiency enchantment
            if (item.hasItemMeta()) {
                ItemMeta itemMeta = item.getItemMeta();
                if (itemMeta.hasEnchant(Enchantment.DIG_SPEED)) {
                    int efficiencyLevel = itemMeta.getEnchantLevel(Enchantment.DIG_SPEED);
                    miningSpeed += (efficiencyLevel * 0.3); // Adjust mining speed based on efficiency level
                }
            }

            if (itemType == Material.DIAMOND_PICKAXE || itemType == Material.DIAMOND_SHOVEL ||
                    itemType == Material.DIAMOND_AXE || itemType == Material.DIAMOND_HOE) {
                miningSpeed += 3.0; // Adjust mining speed for diamond tools
            } else if (itemType == Material.IRON_PICKAXE || itemType == Material.IRON_SHOVEL ||
                    itemType == Material.IRON_AXE || itemType == Material.IRON_HOE) {
                miningSpeed += 2.0; // Adjust mining speed for iron tools
            }

            return (int) miningSpeed;
        }
    
  public static Block getClosestBlockInSight(Player player, int minRange, int maxRange) {
        Location playerLocation = player.getLocation();
        Location targetLocation = playerLocation.clone();
        for (int i = minRange; i <= maxRange; i++) {
            targetLocation.add(playerLocation.getDirection());
            Block block = targetLocation.getBlock();
            if (!block.isEmpty()) {
                return block;
            }
        }
        return null;

  }
public static void placeBlockInSight(Player player, int minRange, int maxRange) {
    ItemStack handItem = player.getInventory().getItemInMainHand();
    if (handItem.getType().isBlock()) {
        Block closestBlock = getClosestBlockInSight(player, minRange, maxRange);
        if (closestBlock != null && closestBlock.getType().isAir() && closestBlock.getType().isSolid()) {
            BlockFace blockFace = getBlockFace(player.getLocation(), closestBlock.getLocation());
            Block placedBlock = closestBlock.getRelative(blockFace);
            placedBlock.setType(handItem.getType());
        }
    }
}

public static BlockFace getBlockFace(Location playerLocation, Location targetLocation) {
        double dx = targetLocation.getX() - playerLocation.getX();
        double dy = targetLocation.getY() - playerLocation.getY();
        double dz = targetLocation.getZ() - playerLocation.getZ();
        double max = Math.max(Math.max(Math.abs(dx), Math.abs(dy)), Math.abs(dz));
        if (max == Math.abs(dx)) {
            return dx > 0 ? BlockFace.EAST : BlockFace.WEST;
        } else if (max == Math.abs(dy)) {
            return dy > 0 ? BlockFace.UP : BlockFace.DOWN;
        } else {
            return dz > 0 ? BlockFace.SOUTH : BlockFace.NORTH;

        }

    }

}
