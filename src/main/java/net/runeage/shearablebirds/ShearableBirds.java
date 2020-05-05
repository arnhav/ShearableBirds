package net.runeage.shearablebirds;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ShearableBirds extends JavaPlugin {

    public static ConsoleCommandSender console;

    public File configFile;
    public static FileConfiguration config;

    @Override
    public void onEnable() {
        // Plugin startup logic

        console = getServer().getConsoleSender();

        console.sendMessage(ChatColor.GOLD + "[" + this.getName() + "] " + ChatColor.AQUA + "+===================+");
        console.sendMessage(ChatColor.GOLD + "[" + this.getName() + "] " + ChatColor.AQUA + "Plugin has been enabled!");
        console.sendMessage(ChatColor.GOLD + "[" + this.getName() + "] " + ChatColor.AQUA + "Version: " + this.getDescription().getVersion());
        console.sendMessage(ChatColor.GOLD + "[" + this.getName() + "] " + ChatColor.AQUA + "+===================+");

        createConfig();

        getServer().getPluginManager().registerEvents(new ShearListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        console.sendMessage(ChatColor.GOLD + "[" + this.getName() + "] " + ChatColor.AQUA + "+===================+");
        console.sendMessage(ChatColor.GOLD + "[" + this.getName() + "] " + ChatColor.AQUA + "Plugin has been disabled!");
        console.sendMessage(ChatColor.GOLD + "[" + this.getName() + "] " + ChatColor.AQUA + "+===================+");
    }

    public void createConfig() {
        try {
            if (!getDataFolder().exists()) {
                getDataFolder().mkdirs();
            }
            configFile = new File(getDataFolder(), "config.yml");
            config = new YamlConfiguration();
            if (!configFile.exists()) {
                getLogger().info("config.yml not found, creating...");
                saveDefaultConfig();
                loadConfig();
            } else {
                getLogger().info("config.yml found, loading...");
                loadConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(){
        config = getConfig();
    }
}
