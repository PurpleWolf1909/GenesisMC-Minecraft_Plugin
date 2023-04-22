package me.dueris.genesismc.core.generation;

import net.minecraft.world.level.levelgen.structure.StructureType;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Structure;

import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static me.dueris.genesismc.core.GenesisMC.waterProtectionEnchant;

public class WaterProtBookGen implements Listener {

@EventHandler
    public void OnGen(LootGenerateEvent e){
    Player p = (Player) e.getEntity();
    if(e.getInventoryHolder() != null){
        if(e.getWorld().canGenerateStructures()){

            Random random = new Random();
            int r = random.nextInt(1000);
            if (r <= 20) {
                e.getLoot().add(new ItemStack(Material.DRAGON_BREATH));
                int l = random.nextInt(4);
                if(l == 1){
                    ItemStack enchbook = new ItemStack(Material.ENCHANTED_BOOK);
                    enchbook.addEnchantment(waterProtectionEnchant, 1);
                    enchbook.setLore(Arrays.asList(ChatColor.GRAY + "Water Protection I"));
                    e.getLoot().add(enchbook);
                }else if(l == 2){
                    ItemStack enchbook = new ItemStack(Material.ENCHANTED_BOOK);
                    enchbook.addEnchantment(waterProtectionEnchant, 1);
                    enchbook.setLore(Arrays.asList(ChatColor.GRAY + "Water Protection II"));
                    e.getLoot().add(enchbook);
                }else if (l == 3){
                    ItemStack enchbook = new ItemStack(Material.ENCHANTED_BOOK);
                    enchbook.addEnchantment(waterProtectionEnchant, 1);
                    enchbook.setLore(Arrays.asList(ChatColor.GRAY + "Water Protection III"));
                    e.getLoot().add(enchbook);
                } else if (l == 4) {
                    ItemStack enchbook = new ItemStack(Material.ENCHANTED_BOOK);
                    enchbook.addEnchantment(waterProtectionEnchant, 1);
                    enchbook.setLore(Arrays.asList(ChatColor.GRAY + "Water Protection IV"));
                    e.getLoot().add(enchbook);
                }

            }




        }
    }
}

}
