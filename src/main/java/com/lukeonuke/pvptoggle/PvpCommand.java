package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.ConfigurationService;
import com.lukeonuke.pvptoggle.service.PvpService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.UUID;

public class PvpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(
            @NotNull CommandSender commandSender,
            @NotNull Command command,
            @NotNull String s,
            String[] args) {
        final ConfigurationService cs = ConfigurationService.getInstance();
        
        if (args.length == 0) {
            if (!(commandSender instanceof Player player)) {
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getConsoleMessage()));
                return true;
            }

            UUID playerId = player.getUniqueId();
            long currentTime = Instant.now().toEpochMilli();

            // Check if the player is on cooldown
            if (PvpService.getCooldownEndTime(playerId) != null && currentTime < PvpService.getCooldownEndTime(playerId)) {
                long remainingCooldown = (PvpService.getCooldownEndTime(playerId) - currentTime) / 1000; // in seconds
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getCooldownMessage()).replace("%s", ChatFormatterService.formatTime(remainingCooldown * 1000)));
                return true;
            }

            // Check if the player can toggle PvP
            if (PvpService.getLastToggleTime(playerId) != null && currentTime < PvpService.getLastToggleTime(playerId) + 3600000) {
                commandSender.sendMessage(ChatFormatterService.addPrefix("§cYou can only toggle PvP once every hour."));
                return true;
            }

            // Toggle PvP status
            boolean isPvpEnabled = PvpService.isPvpEnabled(player);
            PvpService.setPvpEnabled(player, !isPvpEnabled);
            commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getToggleMessage()).replace("%s", ChatFormatterService.booleanHumanReadable(!isPvpEnabled)));

            // Update the last toggle time and set cooldown
            PvpService.setLastToggleTime(playerId, currentTime);
            PvpService.setCooldownEndTime(playerId, currentTime + 28800000); // 8 hours in milliseconds

            // If the player is "silveyynotfound", give them effects
            if (player.getName().equalsIgnoreCase("silveyynotfound") && !isPvpEnabled) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false, false));
                player.sendMessage("§aYou have been granted Regeneration, Resistance, and Strength effects!");
            }
        } else if (args[0].equals("reload")) {
            if (commandSender.hasPermission("pvptoggle.reload")) {
                PvpToggle pluginInstance = (PvpToggle) PvpToggle.plugin;
                pluginInstance.reload();
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getReloadMessage()));
            } else {
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getPermissionMessage()));
            }
        } else {
            if (!commandSender.hasPermission("pvptoggle.pvp.others")) {
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getPermissionMessage()));
                return true;
            }

            for (String arg : args) {
                Player targetPlayer = Bukkit.getPlayer(arg);
                if (targetPlayer == null) {
                    commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getNotFoundMessage().replace("%s", arg)));
                    continue;
                }

                boolean isPvpEnabled = PvpService.isPvpEnabled(targetPlayer);
                PvpService.setPvpEnabled(targetPlayer, !isPvpEnabled);
                commandSender.sendMessage(ChatFormatterService.addPrefix(cs.getRemoteToggleMessage()).replace("%s", targetPlayer.getName()).replace("%r", ChatFormatterService.booleanHumanReadable(!isPvpEnabled)));
            }
        }
        return true;
    }
}
