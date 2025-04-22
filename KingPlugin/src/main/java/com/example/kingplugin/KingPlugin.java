package com.example.kingplugin;

import org.bukkit.Bukkit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class KingPlugin extends JavaPlugin {

    private UUID kingUUID;

    @Override
    public void onEnable() {
        getLogger().info("KingPlugin is ingeschakeld!");

        getCommand("crown").setExecutor(new CrownCommand(this));
        getCommand("resign").setExecutor(new ResignCommand(this));
        getCommand("kinginfo").setExecutor(new KingInfoCommand(this));

        Bukkit.getPluginManager().registerEvents(new KingListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("KingPlugin is uitgeschakeld.");
    }

    public void crownKing(Player player) {
        this.kingUUID = player.getUniqueId();
        player.sendMessage(ChatColor.GOLD + "You are now the king!");
        player.getInventory().addItem(getCrownItem());
    }

    public void clearKing() {
        this.kingUUID = null;
    }

    public UUID getKingUUID() {
        return kingUUID;
    }

    public Player getCurrentKing() {
        return kingUUID != null ? Bukkit.getPlayer(kingUUID) : null;
    }

    public ItemStack getCrownItem() {
        ItemStack crown = new ItemStack(Material.GOLDEN_HELMET);
        ItemMeta meta = crown.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "King's Crown");
            crown.setItemMeta(meta);
        }
        crown.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        crown.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
        return crown;
    }
}
