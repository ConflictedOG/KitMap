package me.conflicted.kitmap.kit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import lombok.Getter;
import lombok.NonNull;
import me.conflicted.kitmap.KitMapPlugin;
import me.conflicted.kitmap.serializable.InventorySerializer;
import me.conflicted.kitmap.serializable.SerializableLocation;

public class KitManager {

	private DBCollection kitsCollection;

	@Getter
	private final List<Kit> kits;

	@NonNull
	private final KitMapPlugin plugin;

	public KitManager(KitMapPlugin plugin) {
		this.kits = new ArrayList<>();
		this.plugin = plugin;
		switch (plugin.getType()) {
		case MONGO:
			this.kitsCollection = plugin.getMongo().getDatabase().getCollection("kits");
			break;
		case FLAT:
			break;
		}
	}

	public Kit getByName(String name) {
		return this.kits.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).findAny().orElse(null);
	}

	public boolean isKit(String name) {
		return this.kits.stream().filter(kit -> kit.getName().equalsIgnoreCase(name)).count() >= 1;
	}

	@SuppressWarnings("unchecked")
	public void load() throws IOException {
		switch (this.plugin.getType()) {
		case FLAT:
			if (this.plugin.getConfig().get("kits") != null)
				for (Kit kit : (List<Kit>) this.plugin.getConfig().get("kits"))
					this.kits.add(kit);
			break;
		case MONGO:
			try (DBCursor cursor = this.kitsCollection.find()) {
				while (cursor.hasNext()) {
					DBObject next = cursor.next();
					Kit kit = new Kit((String) next.get("name"));

					List<Location> kitSigns = new ArrayList<>();
					BasicDBList kitSignsSerialize = (BasicDBList) next.get("kitSigns");

					for (BasicDBObject object : kitSignsSerialize.toArray(new BasicDBObject[0]))
						kitSigns.add(SerializableLocation.deserialize(object.toMap()).getLocation());

					kit.setKitSigns(kitSigns);
					if (next.get("armorContent") != null)
						kit.setArmorContent(InventorySerializer.stacksFromBase64((String) next.get("armorContent")));
					if (next.get("inventoryContent") != null)
						kit.setInventoryContent(
								InventorySerializer.stacksFromBase64((String) next.get("inventoryContent")));

					this.kits.add(kit);
				}
			}
			break;
		default:
			break;
		}
	}

	public void unload() {
		switch (this.plugin.getType()) {
		case FLAT:
			this.plugin.getConfig().set("kits", this.kits);
			this.plugin.saveConfig();
			break;
		case MONGO:
			try (DBCursor cursor = this.kitsCollection.find()) {
				while (cursor.hasNext()) {
					kitsCollection.remove(cursor.next());
				}
			}
			for (Kit kit : this.kits) {
				DBObject doc = new BasicDBObject("name", kit.getName());
				BasicDBList kits = new BasicDBList();

				for (Location location : kit.getKitSigns())
					kits.add(new BasicDBObject(new SerializableLocation(location).serialize()));

				doc.put("kitSigns", kits);
				if (kit.getArmorContent() != null)
					doc.put("armorContent", InventorySerializer.toBase64(kit.getArmorContent()));
				if (kit.getInventoryContent() != null)
					doc.put("inventoryContent", InventorySerializer.toBase64(kit.getInventoryContent()));

				kitsCollection.insert(doc);
			}
			break;
		default:
			break;
		}
	}

	public boolean exists(DBObject object) {
		try (DBCursor cursor = this.kitsCollection.find(object)) {
			return cursor.hasNext();
		}
	}

}
