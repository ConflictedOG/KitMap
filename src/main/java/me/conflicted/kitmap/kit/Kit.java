package me.conflicted.kitmap.kit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;
import me.conflicted.kitmap.serializable.InventorySerializer;
import me.conflicted.kitmap.serializable.SerializableLocation;

public class Kit implements ConfigurationSerializable {

	@Getter
	private final String name;

	@Getter
	@Setter
	private ItemStack[] armorContent;

	@Getter
	@Setter
	private ItemStack[] inventoryContent;

	@Getter
	@Setter
	private List<Location> kitSigns;

	public Kit(String name) {
		this.name = name;
		this.kitSigns = new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public Kit(Map<String, Object> map) throws IOException {
		this.name = (String) map.get("name");
		if (map.get("armorContent") != null)
			this.armorContent = InventorySerializer.stacksFromBase64((String) map.get("armorContent"));
		if (map.get("inventoryContent") != null)
			this.inventoryContent = InventorySerializer.stacksFromBase64((String) map.get("inventoryContent"));
		for (SerializableLocation loc : (List<SerializableLocation>) map.get("kitSigns"))
			this.kitSigns.add(loc.getLocation());
	}

	public static Kit deserialize(Map<String, Object> map) throws IOException {
		return new Kit(map);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		List<SerializableLocation> serialized = new ArrayList<>();

		this.kitSigns.forEach(kitSign -> serialized.add(new SerializableLocation(kitSign)));

		map.put("name", this.name);
		if (armorContent != null)
			map.put("armorContent", InventorySerializer.toBase64(armorContent));
		if (inventoryContent != null)
			map.put("inventoryContent", InventorySerializer.toBase64(inventoryContent));
		map.put("kitSigns", serialized);

		return map;
	}

}
