package com.example.kingplugin;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KingListener implements Listener {

    private final KingPlugin plugin;

    public KingListener(KingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player deceased = event.getEntity();
        Player killer = deceased.getKiller();

        if (plugin.getKingUUID() != null && plugin.getKingUUID().equals(deceased.getUniqueId())) {
            if (killer != null && killer.isOnline()) {
                plugin.crownKing(killer);
                Bukkit.broadcastMessage(ChatColor.RED + deceased.getName() + " has fallen! " + ChatColor.GOLD + killer.getName() + " is the new king!");
            } else {
                plugin.clearKing();
                Bukkit.broadcastMessage(ChatColor.RED + "The king has fallen, and no one has claimed the crown.");
            }
        }
    }
}
