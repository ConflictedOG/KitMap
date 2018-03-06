package me.conflicted.kitmap.database.mongo;

import java.util.Arrays;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import lombok.Getter;
import lombok.NonNull;
import me.conflicted.kitmap.KitMapPlugin;


@SuppressWarnings("deprecation")
public class Mongo {

	private MongoClient mongo = null;
	@Getter
	private DB db = null;
	private Credentials creds;
	
	@NonNull
	private KitMapPlugin plugin;
	
	public Mongo(String username, String password, String database, String host, int port, KitMapPlugin plugin) {
		this.creds = new Credentials(plugin);
		MongoCredential creds = MongoCredential.createCredential(username, database, password.toCharArray());
		mongo = new MongoClient(new ServerAddress(host, port), Arrays.asList(creds));
	}

	public MongoClient getMongo() {
		if (mongo == null)
			new Mongo(creds.user, creds.pass, creds.db, creds.ip, creds.port, KitMapPlugin.getPlugin());
		return mongo;
	}

	public DB getDatabase() {
		if (db == null)
			db = getMongo().getDB(creds.db);
		return db;
	}
	
	public void setDatabase(String name){
		db = getMongo().getDB(name);
	}

	public void close(){
		if(mongo != null)
			mongo.close();
	}
	
}
