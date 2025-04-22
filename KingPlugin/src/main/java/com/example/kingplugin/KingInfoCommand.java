package com.example.kingplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class KingInfoCommand implements CommandExecutor {

    private final KingPlugin plugin;

    public KingInfoCommand(KingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this.");
            return true;
        }

        for (Player p : player.getServer().getOnlinePlayers()) {
            if (plugin.isCrown(p.getInventory().getHelmet())) {
                player.sendMessage(ChatColor.GOLD + "👑 Current King: " + ChatColor.YELLOW + p.getName());

                ItemStack item = plugin.selectedItems.get(p.getUniqueId());
                PotionEffectType effect = plugin.selectedPowers.get(p.getUniqueId());

                if (item != null) {
                    player.sendMessage(ChatColor.AQUA + "⚔ King Item: " + item.getType());
                }
                if (effect != null) {
                    player.sendMessage(ChatColor.LIGHT_PURPLE + "✨ Power: " + effect.getName());
                }
                return true;
            }
        }

        player.sendMessage(ChatColor.GRAY + "There is currently no king.");
        return true;
    }
}
