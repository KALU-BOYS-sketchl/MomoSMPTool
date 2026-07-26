package com.donut.tools;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class DonutCommand implements CommandExecutor {
    private final Main plugin;

    public DonutCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("donuttools.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission (donuttools.admin)!");
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.YELLOW + "Usage: /givetool   ");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not online!");
            return true;
        }

        String type = args[1].toLowerCase();
        long seconds;
        try {
            seconds = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Duration must be a valid number of seconds!");
            return true;
        }

        Material mat;
        String name;
        switch (type) {
            case "pickaxe":
                mat = Material.NETHERITE_PICKAXE;
                name = ChatColor.GOLD + "" + ChatColor.BOLD + "Donut 9x9 Pickaxe";
                break;
            case "axe":
                mat = Material.NETHERITE_AXE;
                name = ChatColor.GOLD + "" + ChatColor.BOLD + "Donut 9x9 Axe";
                break;
            case "shovel":
                mat = Material.NETHERITE_SHOVEL;
                name = ChatColor.GOLD + "" + ChatColor.BOLD + "Donut 9x9 Shovel";
                break;
            case "multitool":
                mat = Material.NETHERITE_PICKAXE;
                name = ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Donut 9x9 MultiTool";
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid tool! Choose pickaxe, axe, shovel, or multitool.");
                return true;
        }

        long expireTime = System.currentTimeMillis() + (seconds * 1000);

        ItemStack tool = new ItemStack(mat);
        ItemMeta meta = tool.getItemMeta();
        meta.setDisplayName(name);

        List lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Mines a " + ChatColor.GREEN + "9x9 area");
        lore.add(ChatColor.RED + "Expires in: " + seconds + " seconds");
        meta.setLore(lore);

        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(new NamespacedKey(plugin, "donut_type"), PersistentDataType.STRING, type);
        data.set(new NamespacedKey(plugin, "expire_time"), PersistentDataType.LONG, expireTime);

        tool.setItemMeta(meta);
        target.getInventory().addItem(tool);

        sender.sendMessage(ChatColor.GREEN + "Gave " + type + " to " + target.getName() + " for " + seconds + "s!");
        return true;
    }
}
