package me.conflicted.kitmap;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongodb.MongoSecurityException;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.conflicted.kitmap.commands.DatabaseCommand;
import me.conflicted.kitmap.commands.KitCommand;
import me.conflicted.kitmap.database.mongo.Credentials;
import me.conflicted.kitmap.database.mongo.Mongo;
import me.conflicted.kitmap.database.type.DatabaseType;
import me.conflicted.kitmap.kit.Kit;
import me.conflicted.kitmap.kit.KitManager;
import me.conflicted.kitmap.listeners.SignListener;
import me.conflicted.kitmap.serializable.SerializableLocation;

public class KitMapPlugin extends JavaPlugin {

	@Getter
	private static KitMapPlugin plugin;

	@Getter
	@Setter
	private DatabaseType type;

	@Getter
	private Mongo mongo;

	@Getter
	private KitManager kitManager;

	@NonNull
	private DatabaseCommand databaseCommand;

	@NonNull
	private KitCommand kitCommand;
	
	@NonNull
	private SignListener signListener;
	
	@Override
	public void onLoad() {
		ConfigurationSerialization.registerClass(SerializableLocation.class);
		ConfigurationSerialization.registerClass(Kit.class);
	}

	@Override
	public void onEnable() {
		KitMapPlugin.plugin = this;
		//
		this.loadConfig();
		this.loadListeners();
		this.loadCommands();
		this.type = DatabaseType.valueOf(this.getConfig().getString("Database-type"));
		switch (type) {
		case MONGO:
			Credentials credentials = new Credentials(this);
			try {
				this.mongo = new Mongo(credentials.user, credentials.pass, credentials.db, credentials.ip,
						credentials.port, this);
				mongo.setDatabase(credentials.db);
			} catch (MongoSecurityException exception) {
				Bukkit.getLogger().info("Failed login on user '" + credentials.user + "'");
			}
			break;
		case FLAT:
			break;
		}
		try {
			(this.kitManager = new KitManager(this)).load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		this.kitManager.unload();
		KitMapPlugin.plugin = null;
	}

	private void loadConfig() {
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	}

	private void loadCommands() {
		this.databaseCommand = new DatabaseCommand(this);
		this.kitCommand = new KitCommand(this);
	}

	private void loadListeners() {
		this.signListener = new SignListener(this);
	}

}
