package com.example.kingplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResignCommand implements CommandExecutor {

    private final KingPlugin plugin;

    public ResignCommand(KingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (plugin.getKingUUID() != null && plugin.getKingUUID().equals(player.getUniqueId())) {
            plugin.clearKing();
            player.getInventory().remove(plugin.getCrownItem());
            player.sendMessage(ChatColor.RED + "You have resigned as king.");
        } else {
            player.sendMessage(ChatColor.GRAY + "You are not the current king.");
        }

        return true;
    }
}
