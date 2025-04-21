package com.example.kingplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class KingPlugin extends JavaPlugin implements Listener {

    private Player king = null;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("maakkoning").setExecutor(new KingCommand());
        getLogger().info("Koning plugin geladen!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Koning plugin uitgeschakeld.");
    }

    private void geefKoningKrachten(Player speler) {
        // Geef kroon
        ItemStack kroon = new ItemStack(Material.GOLDEN_HELMET);
        ItemMeta meta = kroon.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Koning's Kroon");
        kroon.setItemMeta(meta);
        speler.getInventory().setHelmet(kroon);

        // Geef effect
        speler.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1));
        speler.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));

        // Zet als koning
        king = speler;
        Bukkit.broadcastMessage(ChatColor.GOLD + speler.getName() + " is nu de nieuwe Koning!");
    }

    private void verwijderKoningKrachten(Player speler) {
        speler.removePotionEffect(PotionEffectType.REGENERATION);
        speler.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);

        ItemStack helm = speler.getInventory().getHelmet();
        if (helm != null && helm.getType() == Material.GOLDEN_HELMET) {
            ItemMeta meta = helm.getItemMeta();
            if (meta != null && ChatColor.stripColor(meta.getDisplayName()).equals("Koning's Kroon")) {
                speler.getInventory().setHelmet(null);
            }
        }
    }

    public class KingCommand implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.RED + "Gebruik: /maakkoning <speler>");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Speler niet gevonden.");
                return true;
            }

            if (king != null) {
                verwijderKoningKrachten(king);
            }

            geefKoningKrachten(target);
            return true;
        }
    }

    @EventHandler
    public void onKingDeath(PlayerDeathEvent event) {
        Player overleden = event.getEntity();

        if (overleden.equals(king)) {
            Player killer = overleden.getKiller();
            verwijderKoningKrachten(overleden);

            if (killer != null) {
                Bukkit.broadcastMessage(ChatColor.RED + "De Koning " + overleden.getName() + " is verslagen door " + killer.getName() + "!");
                geefKoningKrachten(killer);
            } else {
                Bukkit.broadcastMessage(ChatColor.RED + "De Koning " + overleden.getName() + " is gestorven!");
                king = null;
            }
        }
    }
}