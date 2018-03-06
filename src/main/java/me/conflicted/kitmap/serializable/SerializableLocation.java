package me.conflicted.kitmap.serializable;

import java.util.HashMap;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import lombok.Getter;
import lombok.Setter;

public class SerializableLocation implements ConfigurationSerializable, Bson {

	@Getter
	private Location location;

	@Getter
	private int x, y, z;

	@Getter
	private World world;

	@Getter
	@Setter
	private ObjectId _id;

	public SerializableLocation(World world, int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
		this.location = new Location(world, x, y, z);
	}

	public SerializableLocation(Location location) {
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.world = location.getWorld();
		this.location = location;
	}

	public SerializableLocation(Map<String, Object> map) {
		this.x = (int) map.get("x");
		this.y = (int) map.get("y");
		this.z = (int) map.get("z");
		this.world = Bukkit.getWorld((String) map.get("world"));
		this.location = new Location(this.world, this.x, this.y, this.z);
	}

	public static SerializableLocation deserialize(Map<String, Object> map) {
		return new SerializableLocation(map);
	}

	public SerializableLocation withNewObjectId() {
		set_id(new ObjectId());
		return this;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("x", this.x);
		map.put("y", this.y);
		map.put("z", this.z);
		map.put("world", this.world.getName());

		return map;
	}

	@Override
	public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<SerializableLocation>(this, codecRegistry.get(SerializableLocation.class));
	}

}
