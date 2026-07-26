package com.donut.tools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockBreakListener implements Listener {
    private final Main plugin;

    public BlockBreakListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        NamespacedKey typeKey = new NamespacedKey(plugin, "donut_type");
        NamespacedKey expireKey = new NamespacedKey(plugin, "expire_time");

        if (!data.has(typeKey, PersistentDataType.STRING)) return;

        long expireTime = data.getOrDefault(expireKey, PersistentDataType.LONG, 0L);
        if (System.currentTimeMillis() > expireTime) {
            player.getInventory().setItemInMainHand(null);
            player.sendMessage(ChatColor.RED + "Your Donut 9x9 tool has expired and broke!");
            event.setCancelled(true);
            return;
        }

        String toolType = data.get(typeKey, PersistentDataType.STRING);
        Block centerBlock = event.getBlock();

        // 9x9 Area Calculation (4 blocks each direction from center)
        int radius = 4;
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;

                    Block targetBlock = centerBlock.getRelative(x, y, z);
                    if (canBreak(toolType, targetBlock.getType())) {
                        targetBlock.breakNaturally(item);
                    }
                }
            }
        }
    }

    private boolean canBreak(String toolType, Material mat) {
        if (mat == Material.AIR || mat == Material.BEDROCK || mat == Material.BARRIER) return false;

        switch (toolType) {
            case "pickaxe":
                return Tag.MINEABLE_PICKAXE.isTagged(mat);
            case "axe":
                return Tag.MINEABLE_AXE.isTagged(mat);
            case "shovel":
                return Tag.MINEABLE_SHOVEL.isTagged(mat);
            case "multitool":
                return Tag.MINEABLE_PICKAXE.isTagged(mat) || Tag.MINEABLE_AXE.isTagged(mat) || Tag.MINEABLE_SHOVEL.isTagged(mat);
            default:
                return false;
        }
    }
}
