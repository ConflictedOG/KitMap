package me.conflicted.kitmap.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import lombok.NonNull;
import me.conflicted.kitmap.KitMapPlugin;
import me.conflicted.kitmap.database.type.DatabaseType;
import net.md_5.bungee.api.ChatColor;

public class DatabaseCommand implements CommandExecutor {

	@NonNull
	private final KitMapPlugin plugin;

	public DatabaseCommand(KitMapPlugin plugin) {
		this.plugin = plugin;
		this.plugin.getCommand("database").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (player.hasPermission("kitmap.database.set") || player.hasPermission("kitmap.database.current")
					|| player.hasPermission("kitmap.database.*") || player.hasPermission("kitmap.database.*")) {
				if (args.length == 0 || args.length > 2) {
					player.sendMessage(ChatColor.RED + "Usage: /database <set|current> [database]");
					return true;
				}
				if (args[0].equalsIgnoreCase("set")) {
					if (player.hasPermission("kitmap.database.set") || player.hasPermission("kitmap.database.*")) {
						if (args.length != 2) {
							player.sendMessage(ChatColor.RED + "Usage: /database set <database>");
							return true;
						}
						try {
							DatabaseType type = DatabaseType.valueOf(args[1]);
							this.plugin.getConfig().set("Database-type", type.name());
							this.plugin.saveConfig();
							
							player.sendMessage(ChatColor.GREEN + "Switched database type from "
									+ this.plugin.getType().name() + " to " + type.name());
							this.plugin.setType(type);
						} catch (IllegalArgumentException exception) {
							player.sendMessage(
									ChatColor.RED + "Invalid database type, please choose from the list below:");
							for (DatabaseType types : DatabaseType.values())
								player.sendMessage(ChatColor.RED + types.name());
						}
					} else {
						player.sendMessage(ChatColor.RED + "No permission!");
						return true;
					}
				} else if (args[0].equalsIgnoreCase("current")) {
					if (player.hasPermission("kitmap.database.current") || player.hasPermission("kitmap.database.*")) {
						if (args.length != 1) {
							player.sendMessage(ChatColor.RED + "Usage: /database current");
							return true;
						}
						player.sendMessage(ChatColor.GREEN + "Currently using the database " + plugin.getType().name());
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
