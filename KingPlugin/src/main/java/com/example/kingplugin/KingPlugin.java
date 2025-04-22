package com.example.kingplugin;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class KingPlugin extends JavaPlugin implements Listener {

    private Player currentKing;
    private ItemStack selectedItem;
    private final Map<UUID, PowerType> playerPowers = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        PluginCommand command = getCommand("king");
        if (command != null) command.setExecutor(new KingInfoCommand(this));
        PluginCommand crownCmd = getCommand("crown");
        if (crownCmd != null) crownCmd.setExecutor(new CrownCommand(this));
        startParticleTask();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (victim.equals(currentKing) && killer != null) {
            crownKing(killer);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTitle().equals("Select Item")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && !isToolOrWeapon(clicked)) {
                selectedItem = clicked.clone();
                player.sendMessage(ChatColor.GOLD + "You selected: " + clicked.getType());
                player.closeInventory();
            }
        } else if (event.getView().getTitle().equals("Select Power")) {
            event.setCancelled(true);
            ItemStack clicked = event.getCurrentItem();
            if (clicked != null && clicked.getType() == Material.PAPER) {
                String name = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
                PowerType power = PowerType.valueOf(name.toUpperCase());
                playerPowers.put(player.getUniqueId(), power);
                player.sendMessage(ChatColor.GREEN + "You chose the power: " + power);
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null && killer.equals(currentKing)) {
            openPowerMenu(killer);
        }
    }

    private boolean isToolOrWeapon(ItemStack item) {
        Material type = item.getType();
        return type.name().endsWith("_SWORD") ||
                type.name().endsWith("_AXE") ||
                type.name().endsWith("_PICKAXE") ||
                type.name().endsWith("_SHOVEL") ||
                type.name().endsWith("_HOE") ||
                type == Material.BOW || type == Material.CROSSBOW || type == Material.TRIDENT;
    }

    public void crownKing(Player player) {
        currentKing = player;
        player.getInventory().setHelmet(createCrown());
        player.sendMessage(ChatColor.GOLD + "You are now the King!");
        openItemSelectionMenu(player);
    }

    private ItemStack createCrown() {
        ItemStack crown = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = crown.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "King's Crown");
            meta.setUnbreakable(true);
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
            meta.addEnchant(Enchantment.THORNS, 3, true);
            meta.addEnchant(Enchantment.MENDING, 1, true);
            meta.addEnchant(Enchantment.UNBREAKING, 3, true);
            crown.setItemMeta(meta);
        }
        return crown;
    }

    private boolean isCrown(ItemStack item) {
        return item != null &&
                item.getType() == Material.NETHERITE_HELMET &&
                item.getItemMeta() != null &&
                item.getItemMeta().getDisplayName().contains("King's Crown");
    }

    private void openItemSelectionMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9, "Select Item");
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && !isToolOrWeapon(item)) {
                menu.addItem(item);
            }
        }
        player.openInventory(menu);
    }

    private void openPowerMenu(Player player) {
        Inventory menu = Bukkit.createInventory(null, 9, "Select Power");
        for (PowerType type : PowerType.values()) {
            ItemStack paper = new ItemStack(Material.PAPER);
            ItemMeta meta = paper.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.AQUA + type.name());
                paper.setItemMeta(meta);
            }
            menu.addItem(paper);
        }
        player.openInventory(menu);
    }

    public Player getCurrentKing() {
        return currentKing;
    }

    private void startParticleTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isCrown(player.getInventory().getHelmet())) {
                    player.getWorld().spawnParticle(
                            Particle.REDSTONE,
                            player.getLocation().add(0, 2, 0),
                            10,
                            0.3, 0.3, 0.3,
                            new Particle.DustOptions(Color.ORANGE, 1.5f)
                    );
                }
            }
        }, 0L, 20L);
    }
}
