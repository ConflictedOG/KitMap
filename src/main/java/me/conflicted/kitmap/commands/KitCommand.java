package me.conflicted.kitmap.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.NonNull;
import me.conflicted.kitmap.KitMapPlugin;
import me.conflicted.kitmap.kit.Kit;
import net.md_5.bungee.api.ChatColor;

public class KitCommand implements CommandExecutor {

	@NonNull
	private final KitMapPlugin plugin;

	public KitCommand(KitMapPlugin plugin) {
		this.plugin = plugin;
		plugin.getCommand("kit").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("kitmap.kit.create") || player.hasPermission("kitmap.kit.list")
					|| player.hasPermission("kitmap.kit.delete") || player.hasPermission("kitmap.kit.loadkit")
					|| player.hasPermission("kitmap.kit.*")) {
				if (args.length == 0) {
					player.sendMessage(ChatColor.RED + "Usage: /kit <create|delete|list|setinv|loadkit> [kitName]");
					return true;
				}
				if (args[0].equalsIgnoreCase("create")) {
					if (player.hasPermission("kitmap.kit.create") || player.hasPermission("kitmap.kit.*")) {
						if (args.length != 2) {
							player.sendMessage(ChatColor.RED + "Usage: /kit create <kitName>");
							return true;
						}
						String name = args[1];
						if (this.plugin.getKitManager().isKit(name)) {
							player.sendMessage(ChatColor.RED + "The kit '" + name + "' already exists!");
						} else {
							Kit kit = new Kit(name);
							this.plugin.getKitManager().getKits().add(kit);

							player.sendMessage(ChatColor.GREEN + "Created kit '" + name + "'!");
						}
					} else {
						player.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("setinv")) {
					if (player.hasPermission("kitmap.kit.setinv") || player.hasPermission("kitmap.kit.*")) {
						if (args.length != 2) {
							player.sendMessage(ChatColor.RED + "Usage: /kit setinv <kitName>");
							return true;
						}
						String name = args[1];
						if (this.plugin.getKitManager().isKit(name)) {
							Kit kit = this.plugin.getKitManager().getByName(name);

							kit.setInventoryContent(player.getInventory().getContents());
							kit.setArmorContent(player.getInventory().getArmorContents());

							player.sendMessage(ChatColor.GREEN + "Set kit contents for '" + name + "'");
						} else {
							player.sendMessage(ChatColor.RED + "Could not find kit named '" + name + "'!");
							return true;
						}
					} else {
						player.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("list")) {
					if (player.hasPermission("kitmap.kit.list") || player.hasPermission("kitmap.kit.*")) {
						if (args.length != 1) {
							player.sendMessage(ChatColor.RED + "Usage: /kit list");
							return true;
						}
						player.sendMessage(ChatColor.GREEN + "Current kits: ");
						for (Kit kit : this.plugin.getKitManager().getKits())
							player.sendMessage(ChatColor.YELLOW + "- " + ChatColor.GREEN + kit.getName());
					} else {
						player.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("delete")) {
					if (player.hasPermission("kitmap.kit.delete") || player.hasPermission("kitmap.kit.*")) {
						if (args.length != 2) {
							player.sendMessage(ChatColor.RED + "Usage: /kit delete <kitName>");
							return true;
						}
						String name = args[1];
						if (this.plugin.getKitManager().isKit(name)) {
							this.plugin.getKitManager().getKits().remove(this.plugin.getKitManager().getByName(name));

							if (this.plugin.getKitManager().isKit(name)) {
								player.sendMessage(ChatColor.RED + "Failed deleting kit, try again!");
								return true;
							} else {
								player.sendMessage(ChatColor.GREEN + "Successfully deleted kit '" + name + "'!");
							}
						} else {
							player.sendMessage(ChatColor.RED + "Could not find kit named '" + name + "'!");
							return true;
						}
					} else {
						player.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("loadkit")) {
					if (player.hasPermission("kitmap.kit.loadkit") || player.hasPermission("kitmap.kit.*")) {
						if (args.length != 2) {
							player.sendMessage(ChatColor.RED + "Usage: /kit loadkit <kitName>");
							return true;
						}
						String name = args[1];
						if (this.plugin.getKitManager().isKit(name)) {
							Kit kit = this.plugin.getKitManager().getByName(name);

							player.getInventory().clear();
							player.getInventory().setArmorContents(null);
							if (kit.getInventoryContent() != null)
								player.getInventory().setContents(kit.getInventoryContent());
							if (kit.getArmorContent() != null)
								player.getInventory().setArmorContents(kit.getArmorContent());
							player.updateInventory();

							player.sendMessage(ChatColor.GREEN + "Loaded kit '" + name + "'.");
						} else {
							player.sendMessage(ChatColor.RED + "Could not find kit named '" + name + "'!");
							return true;
						}
					} else {
						player.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + "Unknown sub-command!");
					return true;
				}
			} else {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
		}
		return false;
	}

}
