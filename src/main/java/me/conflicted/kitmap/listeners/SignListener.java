package me.conflicted.kitmap.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import lombok.NonNull;
import me.conflicted.kitmap.KitMapPlugin;
import me.conflicted.kitmap.kit.Kit;
import net.md_5.bungee.api.ChatColor;

public class SignListener implements Listener {

	@NonNull
	private final KitMapPlugin plugin;

	public SignListener(KitMapPlugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block block = event.getClickedBlock();

		if (action == Action.RIGHT_CLICK_BLOCK) {
			if (block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST
					|| block.getType() == Material.WALL_SIGN) {
				Sign sign = (Sign)block.getState();
				
				for(Kit kit : this.plugin.getKitManager().getKits()) {
					if(kit.getKitSigns().contains(sign.getLocation())) {
						if(player.hasPermission("kitmap.kit." + kit.getName())) {
							player.getInventory().clear();
							player.getInventory().setArmorContents(null);
							if(kit.getInventoryContent() != null)
							player.getInventory().setContents(kit.getInventoryContent());
							if(kit.getArmorContent() != null)
							player.getInventory().setArmorContents(kit.getArmorContent());
							player.updateInventory();
							
							player.sendMessage(ChatColor.DARK_AQUA + "Equipped kit " + ChatColor.AQUA + kit.getName());
						} else {
							player.sendMessage(ChatColor.RED + "No permission!");
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreate(SignChangeEvent event) {
		if (event.getLines()[0].equalsIgnoreCase("[kit]")) {
			Player player = event.getPlayer();
			if (player.hasPermission("kitmap.sign.create")) {
				String kitName = event.getLines()[1];
				if (this.plugin.getKitManager().isKit(kitName)) {
					Kit kit = this.plugin.getKitManager().getByName(kitName);
					Location location = event.getBlock().getLocation();

					kit.getKitSigns().add(location);

					String bar = ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString()
							+ "--------------------------------";

					event.setLine(0, bar);
					event.setLine(1, ChatColor.DARK_AQUA + "Kit: " + ChatColor.AQUA + kit.getName());
					event.setLine(2, ChatColor.DARK_AQUA + "Equip");
					event.setLine(3, bar);

					player.sendMessage(ChatColor.GREEN + "Created kit sign for kit '" + kitName + "' at "
							+ location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
				} else {
					player.sendMessage(ChatColor.RED + "Kit '" + kitName + "' could not be found!");
				}
			}
		}
	}

}
