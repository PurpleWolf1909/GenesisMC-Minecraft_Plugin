package me.dueris.genesismc.core.factory.powers.OriginsMod.player.inventory;

import me.dueris.genesismc.core.GenesisMC;
import me.dueris.genesismc.core.commands.subcommands.SubCommand;
import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.events.KeybindTriggerEvent;
import me.dueris.genesismc.core.events.OriginChangeEvent;
import me.dueris.genesismc.core.utils.BukkitColour;
import me.dueris.genesismc.core.utils.OriginContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

import static me.dueris.genesismc.core.KeybindHandler.isKeyBeingPressed;
import static me.dueris.genesismc.core.factory.powers.Powers.shulker_inventory;

public class Inventory implements CommandExecutor, Listener {

    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public Inventory() {
    }

    @EventHandler
    public void MoveBackChange(OriginChangeEvent e) {
        Player p = e.getPlayer();
        for (OriginContainer origin : OriginPlayer.getOrigin(p).values()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!shulker_inventory.contains(p)) {
                        ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(p);
                        org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.CHEST, "origin.getPowerFileFromType(origins:inventory).get(title)");

                        vaultItems.stream()
                                .forEach(itemStack -> vault.addItem(itemStack));
                        for (ItemStack item : vault.getContents()) {
                            if (item != null && item.getType() != Material.AIR) {
                                p.getWorld().dropItemNaturally(p.getLocation(), item);
                                vault.removeItem(item);
                            }
                        }
                        ArrayList<ItemStack> prunedItems = new ArrayList<>();

                        Arrays.stream(vault.getContents())
                                .filter(itemStack -> {
                                    return itemStack != null;
                                })
                                .forEach(itemStack -> prunedItems.add(itemStack));

                        InventoryUtils.storeItems(prunedItems, p);
                        vault.clear();
                        this.cancel();
                    }
                }
            }.runTaskTimer(GenesisMC.getPlugin(), 1L, 1L);
        }

    }

    @EventHandler
    public void keytrigger(KeybindTriggerEvent e) {
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            if (shulker_inventory.contains(e.getPlayer())) {
                if (isKeyBeingPressed(e.getPlayer(), origin.getPowerFileFromType("origins:inventory").getKey().get("key").toString(), true)) {
                    ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(e.getPlayer());
                    org.bukkit.inventory.Inventory vault = Bukkit.createInventory(e.getPlayer(), InventoryType.valueOf(origin.getPowerFileFromType("origins:inventory").get("container_type", "chest").toUpperCase()), origin.getPowerFileFromType("origins:inventory").get("title", "inventory.container.title").replace("%player%", e.getPlayer().getName()));
                    vaultItems.stream().forEach(itemStack -> vault.addItem(itemStack));
                    e.getPlayer().openInventory(vault);

                }
            }
        }
    }

    @EventHandler
    public void deathTIMEEE(PlayerDeathEvent e) {
        for (OriginContainer origin : OriginPlayer.getOrigin(e.getPlayer()).values()) {
            if (shulker_inventory.contains(e.getPlayer())) {
                Player p = e.getPlayer();
                if (origin.getPowerFileFromType("origins:inventory").getDropOnDeath()) {
                    ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(p);
                    org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.CHEST, "origin.getPowerFileFromType(origins:inventory).get(title)");

                    vaultItems.stream()
                            .forEach(itemStack -> vault.addItem(itemStack));
                    for (ItemStack item : vault.getContents()) {
                        if (item != null && item.getType() != Material.AIR) {
                            p.getWorld().dropItemNaturally(p.getLocation(), item);
                            vault.removeItem(item);
                        }
                    }
                    ArrayList<ItemStack> prunedItems = new ArrayList<>();

                    Arrays.stream(vault.getContents())
                            .filter(itemStack -> {
                                return itemStack != null;
                            })
                            .forEach(itemStack -> prunedItems.add(itemStack));

                    InventoryUtils.storeItems(prunedItems, p);
                    vault.clear();
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player p) {
            //opens target players shulk inventory
            if (args.length >= 2 && p.hasPermission("genesism.origins.cmd.othershulk")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target != null) {
                    ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(target);
                    org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory: " + target.getName());
                    vaultItems.stream().forEach(itemStack -> vault.addItem(itemStack));
                    p.openInventory(vault);
                    return true;
                }
            }

            //opens own shulk inventory
            if (shulker_inventory.contains((Player) sender)) {
                ArrayList<ItemStack> vaultItems = InventoryUtils.getItems(p);
                org.bukkit.inventory.Inventory vault = Bukkit.createInventory(p, InventoryType.DROPPER, "Shulker Inventory: " + p.getName());
                vaultItems.stream().forEach(itemStack -> vault.addItem(itemStack));
                p.openInventory(vault);
            } else {
                p.sendMessage(Component.text("powers.inventoryOpenPower").color(TextColor.fromHexString(BukkitColour.RED)));
            }
        }


        return true;
    }
}

