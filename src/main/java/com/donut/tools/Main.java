package com.donut.tools;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        getCommand("givetool").setExecutor(new DonutCommand(this));
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getLogger().info("DonutTools Plugin Enabled!");
    }
}
