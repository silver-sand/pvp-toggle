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

import java.util.Objects;

public final class PvpToggle extends JavaPlugin {
    @Getter
    public static Plugin plugin = null;

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
}
