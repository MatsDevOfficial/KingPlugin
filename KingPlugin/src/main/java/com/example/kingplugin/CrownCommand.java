package com.example.kingplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CrownCommand implements CommandExecutor {

    private final KingPlugin plugin;

    public CrownCommand(KingPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("kingplugin.command.crown")) {
            sender.sendMessage(ChatColor.RED + "You don’t have permission to use this command.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /crown <player>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        plugin.crownKing(target);
        Bukkit.broadcastMessage(ChatColor.GOLD + target.getName() + " has been crowned as the new King!");
        return true;
    }
}
