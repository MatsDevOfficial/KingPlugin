package com.example.kingplugin;

import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.*;

import java.util.*;

public class KingPlugin extends JavaPlugin implements Listener {

    public final String CROWN_NAME = ChatColor.GOLD + "King's Crown";
    public final Map<UUID, ItemStack> selectedItems = new HashMap<>();
    public final Map<UUID, PotionEffectType> selectedPowers = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("makeking").setExecutor(new KingCommand());
        getCommand("king").setExecutor(new KingInfoCommand(this));
        startParticleTask();
        getLogger().info("King Plugin enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("King Plugin disabled.");
    }

    private ItemStack createCrown() {
        ItemStack crown = new ItemStack(Material.NETHERITE_HELMET);
        ItemMeta meta = crown.getItemMeta();
        meta.setDisplayName(CROWN_NAME);
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.addEnchant(Enchantment.MENDING, 1, true);
        crown.setItemMeta(meta);
        return crown;
    }

    private boolean isCrown(ItemStack item) {
        return item != null &&
                item.getType() == Material.NETHERITE_HELMET &&
                item.hasItemMeta() &&
                item.getItemMeta().getDisplayName().equals(CROWN_NAME);
    }

    private boolean isToolOrWeapon(Material material) {
        return material.toString().endsWith("_SWORD") ||
                material.toString().endsWith("_AXE") ||
                material.toString().endsWith("_PICKAXE") ||
                material.toString().endsWith("_SHOVEL") ||
                material.toString().endsWith("_HOE") ||
                material.toString().contains("BOW") ||
                material.toString().contains("TRIDENT") ||
                material.toString().contains("CROSSBOW");
    }

    public class KingCommand implements CommandExecutor {
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length != 1) {
                sender.sendMessage("Usage: /makeking <player>");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage("Player not found.");
                return true;
            }

            ItemStack crown = createCrown();
            target.getInventory().addItem(crown);
            target.sendMessage(ChatColor.GOLD + "You have received the King's Crown!");
            return true;
        }
    }

    @EventHandler
    public void onHelmetChange(PlayerInventoryEvent e) {
        Bukkit.getScheduler().runTaskLater(this, () -> checkKingStatus(e.getPlayer()), 1L);
    }

    private void checkKingStatus(Player player) {
        if (isCrown(player.getInventory().getHelmet())) {
            openItemSelectMenu(player);
        } else {
            removeKingEffects(player);
        }
    }

    private void openItemSelectMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Choose your King Item");

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR && !isToolOrWeapon(item.getType())) {
                inv.addItem(item.clone());
            }
        }
        player.openInventory(inv);
    }

    private void openPowerMenu(Player player) {
        Inventory powerInv = Bukkit.createInventory(null, 9, ChatColor.RED + "Choose your Power");

        powerInv.setItem(0, new ItemStack(Material.POTION)); // Regeneration
        powerInv.setItem(1, new ItemStack(Material.IRON_CHESTPLATE)); // Resistance
        powerInv.setItem(2, new ItemStack(Material.SUGAR)); // Speed
        powerInv.setItem(3, new ItemStack(Material.BLAZE_POWDER)); // Strength
        powerInv.setItem(4, new ItemStack(Material.MAGMA_CREAM)); // Fire Resistance

        player.openInventory(powerInv);
    }

    private void givePowerIfHolding(Player player) {
        ItemStack inHand = player.getInventory().getItemInMainHand();
        ItemStack selected = selectedItems.get(player.getUniqueId());
        PotionEffectType power = selectedPowers.get(player.getUniqueId());

        removeKingEffects(player);

        if (selected != null && power != null && inHand != null &&
                inHand.getType() == selected.getType()) {
            player.addPotionEffect(new PotionEffect(power, Integer.MAX_VALUE, 1));
        }
    }

    private void removeKingEffects(Player player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getView().getTitle().contains("Choose
