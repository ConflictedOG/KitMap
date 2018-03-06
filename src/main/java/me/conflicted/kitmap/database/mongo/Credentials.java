package me.conflicted.kitmap.database.mongo;

import lombok.NonNull;
import me.conflicted.kitmap.KitMapPlugin;

public class Credentials {

	public String user;
	public String pass;
	public String ip;
	public int port;
	public String db;
	
	@NonNull
	private KitMapPlugin plugin;
	
	public Credentials(KitMapPlugin plugin){
		this.plugin = plugin;
		user = plugin.getConfig().getString("Database.user");
		pass = plugin.getConfig().getString("Database.pass");
		db = plugin.getConfig().getString("Database.database");
		port = plugin.getConfig().getInt("Database.port");
		ip = plugin.getConfig().getString("Database.host");
	}
	
}
