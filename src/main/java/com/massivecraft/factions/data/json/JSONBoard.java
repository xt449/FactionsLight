package com.massivecraft.factions.data.json;

import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.MemoryBoard;
import com.massivecraft.factions.util.DiscUtil;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;


public class JSONBoard extends MemoryBoard {
	private static final transient File file = new File(FactionsPlugin.getInstance().getDataFolder(), "data/board.json");

	// -------------------------------------------- //
	// Persistance
	// -------------------------------------------- //

	public Map<String, Map<String, Integer>> dumpAsSaveFormat() {
		Map<String, Map<String, Integer>> worldCoordIds = new HashMap<>();

		String worldName, coords;
		Integer id;

		for(Entry<FLocation, Integer> entry : flocationIds.entrySet()) {
			worldName = entry.getKey().getWorldName();
			coords = entry.getKey().getCoordString();
			id = entry.getValue();
			if(!worldCoordIds.containsKey(worldName)) {
				worldCoordIds.put(worldName, new TreeMap<>());
			}

			worldCoordIds.get(worldName).put(coords, id);
		}

		return worldCoordIds;
	}

	public void loadFromSaveFormat(Map<String, Map<String, Integer>> worldCoordIds) {
		flocationIds.clear();

		String worldName;
		String[] coords;
		int x, z;
		Integer factionId;

		for(Entry<String, Map<String, Integer>> entry : worldCoordIds.entrySet()) {
			worldName = entry.getKey();
			for(Entry<String, Integer> entry2 : entry.getValue().entrySet()) {
				coords = entry2.getKey().trim().split("[,\\s]+");
				x = Integer.parseInt(coords[0]);
				z = Integer.parseInt(coords[1]);
				factionId = entry2.getValue();
				flocationIds.put(new FLocation(worldName, x, z), factionId);
			}
		}
	}

	public void forceSave() {
		forceSave(true);
	}

	public void forceSave(boolean sync) {
		DiscUtil.writeCatch(file, FactionsPlugin.getInstance().gson.toJson(dumpAsSaveFormat()), sync);
	}

	public int load() {
		if(!file.exists()) {
			FactionsPlugin.getInstance().getLogger().info("No board to load from disk. Creating new file.");
			forceSave();
			return 0;
		}

		try {
			Type type = new TypeToken<Map<String, Map<String, Integer>>>() {
			}.getType();
			Map<String, Map<String, Integer>> worldCoordIds = FactionsPlugin.getInstance().gson.fromJson(DiscUtil.read(file), type);
			loadFromSaveFormat(worldCoordIds);
		} catch(Exception e) {
			FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to load the board from disk.", e);
			return 0;
		}

		return flocationIds.size();
	}
}
