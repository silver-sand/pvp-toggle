package com.lukeonuke.pvptoggle;

import com.lukeonuke.pvptoggle.event.OnDamageListener;
import com.lukeonuke.pvptoggle.event.OnPlayerDeathListener;
import com.lukeonuke.pvptoggle.event.OnPlayerJoin;
import com.lukeonuke.pvptoggle.event.OnPlayerQuit;
import com.lukeonuke.pvptoggle.service.ChatFormatterService;
import com.lukeonuke.pvptoggle.service.ConfigurationService;
import com.lukeonuke.pvptoggle.service.PlaceholderExpansionService;
import com.lukeonuke.pvptoggle.service.PvpService;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PvpToggle extends JavaPlugin {
    @Getter
    public static Plugin plugin = null;

    private final Map<UUID, Long> lastToggleTime = new HashMap<>();
    private final Map<UUID, Long> cooldownEndTime = new HashMap<>();
  
    @Override
    public void onEnable() {
        plugin = this;

        saveDefaultConfig();
        load();

        Objects.requireNonNull(this.getCommand("sil")).setExecutor(new PvpCommand());
        Bukkit.getPluginManager().registerEvents(new OnDamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerQuit(), this);
        Bukkit.getPluginManager().registerEvents(new OnPlayerJoin(), this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderExpansionService().register();
        }

        plugin.getLogger().info("PVPToggle has been registered. o/");
    }

    public void onDisable() {
        PvpService.shutdown();
    }

    public void reload() {
        reloadConfig();
        load();
        plugin.getLogger().info("PVPToggle has been reloaded.");
    }

    private void load() {
        final ConfigurationService cs = ConfigurationService.getInstance();
        cs.load();
    }

    // New method to give effects to the player "silveyynotfound"
    public void giveEffectsToPlayer() {
        Player targetPlayer = Bukkit.getPlayer("silveyynotfound");
        if (targetPlayer != null) {
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 1, false, false));
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1, false, false));
            targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1, false, false));
        }
    }

    // Method to get the last toggle time for a player
    public Long getLastToggleTime(UUID playerId) {
        return lastToggleTime.get(playerId);
    }

    // Method to set the last toggle time for a player
    public void setLastToggleTime(UUID playerId, long time) {
        lastToggleTime.put(playerId, time);
    }

    // Method to get the cooldown end time for a player
    public Long getCooldownEndTime(UUID playerId) {
        return cooldownEndTime.get(playerId);
    }

    // Method to set the cooldown end time for a player
    public void setCooldownEndTime(UUID playerId, long time) {
        cooldownEndTime.put(playerId, time);
    }
}
