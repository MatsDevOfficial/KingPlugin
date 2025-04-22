package com.example.kingplugin;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class KingInfoCommand implements CommandExecutor {

    private final KingPlugin plugin;

    public KingInfoCommand(KingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this.");
            return true;
        }
        Player player = (Player) sender;

        Player king = plugin.getCurrentKing();
        if (king == null) {
            player.sendMessage(ChatColor.GRAY + "There is currently no King.");
        } else {
            player.sendMessage(ChatColor.GOLD + "The current King is: " + ChatColor.YELLOW + king.getName());
        }
        return true;
    }
}
