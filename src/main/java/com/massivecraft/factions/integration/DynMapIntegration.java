package com.massivecraft.factions.integration;

import com.massivecraft.factions.*;
import com.massivecraft.factions.configuration.DynMapConfiguration;
import com.massivecraft.factions.data.MemoryBoard;
import com.massivecraft.factions.integration.dynmap.DynMapStyle;
import com.massivecraft.factions.integration.dynmap.TempAreaMarker;
import com.massivecraft.factions.integration.dynmap.TempMarkerSet;
import com.massivecraft.factions.perms.Role;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.dynmap.markers.PlayerSet;
import org.dynmap.utils.TileFlags;

import java.util.*;
import java.util.Map.Entry;

// This source code is a heavily modified version of mikeprimms plugin Dynmap-Factions.
public class DynMapIntegration {
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //

	public final static int BLOCKS_PER_CHUNK = 16;

	public final static String DYNMAP_INTEGRATION = "\u00A7dDynmap Integration: \u00A7e";

	public final static String FACTIONS = "factions";
	public final static String FACTIONS_ = FACTIONS + "_";

	public final static String FACTIONS_MARKERSET = FACTIONS_ + "markerset";

	public final static String FACTIONS_PLAYERSET = FACTIONS_ + "playerset";
	public final static String FACTIONS_PLAYERSET_ = FACTIONS_PLAYERSET + "_";

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static final DynMapIntegration instance = new DynMapIntegration();
	private final DynMapConfiguration config = FactionsPlugin.getInstance().configDynMap;

	public static DynMapIntegration getInstance() {
		return instance;
	}

	public DynmapAPI dynmapApi;
	public MarkerAPI markerApi;
	public MarkerSet markerset;

	public void init(Plugin dynmap) {
		this.dynmapApi = (DynmapAPI) dynmap;

		// Should we even use dynmap?
		if(!config.dynmap().isEnabled()) {
			if(this.markerset != null) {
				this.markerset.deleteMarkerSet();
				this.markerset = null;
			}
			return;
		}

		// Schedule non thread safe sync at the end!
		Bukkit.getScheduler().scheduleSyncRepeatingTask(FactionsPlugin.getInstance(), () -> {

			final Map<String, TempAreaMarker> areas = createAreas();
			final Map<String, Set<String>> playerSets = createPlayersets();

			if(!updateCore()) {
				return;
			}

			// createLayer() is thread safe but it makes use of fields set in updateCore() so we must have it after.
			if(!updateLayer(createLayer())) {
				return;
			}

			updateAreas(areas);
			updatePlayersets(playerSets);
		}, 100L, 100L);

		FactionsPlugin.getInstance().getLogger().info("Enabled Dynmap integration");
	}

	// Thread Safe / Asynchronous: No
	public boolean updateCore() {
		// Get DynmapAPI
		this.dynmapApi = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
		if(this.dynmapApi == null) {
			logSevere("Could not retrieve the DynmapAPI.");
			return false;
		}

		// Get MarkerAPI
		this.markerApi = this.dynmapApi.getMarkerAPI();
		if(this.markerApi == null) {
			logSevere("Could not retrieve the MarkerAPI.");
			return false;
		}

		return true;
	}

	// Thread Safe / Asynchronous: Yes
	public TempMarkerSet createLayer() {
		TempMarkerSet ret = new TempMarkerSet();
		ret.label = config.dynmap().getLayerName();
		ret.minimumZoom = config.dynmap().getLayerMinimumZoom();
		ret.priority = config.dynmap().getLayerPriority();
		ret.hideByDefault = !config.dynmap().isLayerVisible();
		return ret;
	}

	// Thread Safe / Asynchronous: No
	public boolean updateLayer(TempMarkerSet temp) {
		this.markerset = this.markerApi.getMarkerSet(FACTIONS_MARKERSET);
		if(this.markerset == null) {
			this.markerset = temp.create(this.markerApi, FACTIONS_MARKERSET);
			if(this.markerset == null) {
				logSevere("Could not create the Faction Markerset/Layer");
				return false;
			}
		} else {
			temp.update(this.markerset);
		}
		return true;
	}

	// -------------------------------------------- //
	// UPDATE: AREAS
	// -------------------------------------------- //

	// Thread Safe: YES
	public Map<String, TempAreaMarker> createAreas() {
		Map<String, Map<Faction, Set<FLocation>>> worldFactionChunks = createWorldFactionChunks();
		return createAreas(worldFactionChunks);
	}

	// Thread Safe: YES
	public Map<String, Map<Faction, Set<FLocation>>> createWorldFactionChunks() {
		// Create map "world name --> faction --> set of chunk coords"
		Map<String, Map<Faction, Set<FLocation>>> worldFactionChunks = new HashMap<>();

		// Note: The board is the world. The board id is the world name.
		MemoryBoard board = (MemoryBoard) Board.getInstance();

		for(Entry<FLocation, Integer> entry : board.flocationIds.entrySet()) {
			String world = entry.getKey().getWorldName();
			Faction chunkOwner = Factions.getInstance().getFactionById(entry.getValue());

			Map<Faction, Set<FLocation>> factionChunks = worldFactionChunks.computeIfAbsent(world, k -> new HashMap<>());

			Set<FLocation> factionTerritory = factionChunks.computeIfAbsent(chunkOwner, k -> new HashSet<>());

			factionTerritory.add(entry.getKey());
		}

		return worldFactionChunks;
	}

	// Thread Safe: YES
	public Map<String, TempAreaMarker> createAreas(Map<String, Map<Faction, Set<FLocation>>> worldFactionChunks) {
		Map<String, TempAreaMarker> ret = new HashMap<>();

		// For each world
		for(Entry<String, Map<Faction, Set<FLocation>>> entry : worldFactionChunks.entrySet()) {
			String world = entry.getKey();
			Map<Faction, Set<FLocation>> factionChunks = entry.getValue();

			// For each faction and its chunks in that world
			for(Entry<Faction, Set<FLocation>> entry1 : factionChunks.entrySet()) {
				Faction faction = entry1.getKey();
				Set<FLocation> chunks = entry1.getValue();
				Map<String, TempAreaMarker> worldFactionMarkers = createAreas(world, faction, chunks);
				ret.putAll(worldFactionMarkers);
			}
		}

		return ret;
	}

	// Thread Safe: YES
	// Handle specific faction on specific world
	// "handle faction on world"
	public Map<String, TempAreaMarker> createAreas(String world, Faction faction, Set<FLocation> chunks) {
		Map<String, TempAreaMarker> ret = new HashMap<>();

		// If the faction is visible ...
		if(!isVisible(faction)) {
			return ret;
		}

		// ... and has any chunks ...
		if(chunks.isEmpty()) {
			return ret;
		}

		// Index of polygon for given faction
		int markerIndex = 0;

		// Create the info window
		String description = getDescription(faction);

		// Fetch Style
		DynMapStyle style = this.getStyle(faction);

		// Loop through chunks: set flags on chunk map
		TileFlags allChunkFlags = new TileFlags();
		LinkedList<FLocation> allChunks = new LinkedList<>();
		for(FLocation chunk : chunks) {
			allChunkFlags.setFlag((int) chunk.getX(), (int) chunk.getZ(), true); // Set flag for chunk
			allChunks.addLast(chunk);
		}

		// Loop through until we don't find more areas
		while(allChunks != null) {
			TileFlags ourChunkFlags = null;
			LinkedList<FLocation> ourChunks = null;
			LinkedList<FLocation> newChunks = null;

			int minimumX = Integer.MAX_VALUE;
			int minimumZ = Integer.MAX_VALUE;
			for(FLocation chunk : allChunks) {
				int chunkX = (int) chunk.getX();
				int chunkZ = (int) chunk.getZ();

				// If we need to start shape, and this block is not part of one yet
				if(ourChunkFlags == null && allChunkFlags.getFlag(chunkX, chunkZ)) {
					ourChunkFlags = new TileFlags(); // Create map for shape
					ourChunks = new LinkedList<>();
					floodFillTarget(allChunkFlags, ourChunkFlags, chunkX, chunkZ); // Copy shape
					ourChunks.add(chunk); // Add it to our chunk list
					minimumX = chunkX;
					minimumZ = chunkZ;
				}
				// If shape found, and we're in it, add to our node list
				else if(ourChunkFlags != null && ourChunkFlags.getFlag(chunkX, chunkZ)) {
					ourChunks.add(chunk);
					if(chunkX < minimumX) {
						minimumX = chunkX;
						minimumZ = chunkZ;
					} else if(chunkX == minimumX && chunkZ < minimumZ) {
						minimumZ = chunkZ;
					}
				}
				// Else, keep it in the list for the next polygon
				else {
					if(newChunks == null) {
						newChunks = new LinkedList<>();
					}
					newChunks.add(chunk);
				}
			}

			// Replace list (null if no more to process)
			allChunks = newChunks;

			if(ourChunkFlags == null) {
				continue;
			}

			// Trace outline of blocks - start from minx, minz going to x+
			int initialX = minimumX;
			int initialZ = minimumZ;
			int currentX = minimumX;
			int currentZ = minimumZ;
			Direction direction = Direction.XPLUS;
			ArrayList<int[]> linelist = new ArrayList<>();
			linelist.add(new int[] {initialX, initialZ}); // Add start point
			while((currentX != initialX) || (currentZ != initialZ) || (direction != Direction.ZMINUS)) {
				switch(direction) {
					case XPLUS: // Segment in X+ direction
						if(!ourChunkFlags.getFlag(currentX + 1, currentZ)) { // Right turn?
							linelist.add(new int[] {currentX + 1, currentZ}); // Finish line
							direction = Direction.ZPLUS; // Change direction
						} else if(!ourChunkFlags.getFlag(currentX + 1, currentZ - 1)) { // Straight?
							currentX++;
						} else { // Left turn
							linelist.add(new int[] {currentX + 1, currentZ}); // Finish line
							direction = Direction.ZMINUS;
							currentX++;
							currentZ--;
						}
						break;
					case ZPLUS: // Segment in Z+ direction
						if(!ourChunkFlags.getFlag(currentX, currentZ + 1)) { // Right turn?
							linelist.add(new int[] {currentX + 1, currentZ + 1}); // Finish line
							direction = Direction.XMINUS; // Change direction
						} else if(!ourChunkFlags.getFlag(currentX + 1, currentZ + 1)) { // Straight?
							currentZ++;
						} else { // Left turn
							linelist.add(new int[] {currentX + 1, currentZ + 1}); // Finish line
							direction = Direction.XPLUS;
							currentX++;
							currentZ++;
						}
						break;
					case XMINUS: // Segment in X- direction
						if(!ourChunkFlags.getFlag(currentX - 1, currentZ)) { // Right turn?
							linelist.add(new int[] {currentX, currentZ + 1}); // Finish line
							direction = Direction.ZMINUS; // Change direction
						} else if(!ourChunkFlags.getFlag(currentX - 1, currentZ + 1)) { // Straight?
							currentX--;
						} else { // Left turn
							linelist.add(new int[] {currentX, currentZ + 1}); // Finish line
							direction = Direction.ZPLUS;
							currentX--;
							currentZ++;
						}
						break;
					case ZMINUS: // Segment in Z- direction
						if(!ourChunkFlags.getFlag(currentX, currentZ - 1)) { // Right turn?
							linelist.add(new int[] {currentX, currentZ}); // Finish line
							direction = Direction.XPLUS; // Change direction
						} else if(!ourChunkFlags.getFlag(currentX - 1, currentZ - 1)) { // Straight?
							currentZ--;
						} else { // Left turn
							linelist.add(new int[] {currentX, currentZ}); // Finish line
							direction = Direction.XMINUS;
							currentX--;
							currentZ--;
						}
						break;
				}
			}

			int sz = linelist.size();
			double[] x = new double[sz];
			double[] z = new double[sz];
			for(int i = 0; i < sz; i++) {
				int[] line = linelist.get(i);
				x[i] = (double) line[0] * (double) BLOCKS_PER_CHUNK;
				z[i] = (double) line[1] * (double) BLOCKS_PER_CHUNK;
			}

			// Build information for specific area
			String markerId = FACTIONS_ + world + "__" + faction.getId() + "__" + markerIndex;

			TempAreaMarker temp = new TempAreaMarker();
			temp.label = faction.getTag();
			temp.world = world;
			temp.x = x;
			temp.z = z;
			temp.description = description;

			temp.lineColor = style.getLineColor();
			temp.lineOpacity = style.getLineOpacity();
			temp.lineWeight = style.getLineWeight();

			temp.fillColor = style.getFillColor();
			temp.fillOpacity = style.getFillOpacity();

			temp.boost = style.getBoost();

			ret.put(markerId, temp);

			markerIndex++;
		}

		return ret;
	}

	// Thread Safe: NO
	public void updateAreas(Map<String, TempAreaMarker> areas) {
		// Map Current
		Map<String, AreaMarker> markers = new HashMap<>();
		for(AreaMarker marker : this.markerset.getAreaMarkers()) {
			markers.put(marker.getMarkerID(), marker);
		}

		// Loop New
		for(Entry<String, TempAreaMarker> entry : areas.entrySet()) {
			String markerId = entry.getKey();
			TempAreaMarker temp = entry.getValue();

			// Get Creative
			// NOTE: I remove from the map created just in the beginning of this method.
			// NOTE: That way what is left at the end will be outdated markers to remove.
			AreaMarker marker = markers.remove(markerId);
			if(marker == null) {
				marker = temp.create(this.markerset, markerId);
				if(marker == null) {
					logSevere("Could not get/create the area marker " + markerId);
				}
			} else {
				temp.update(marker);
			}
		}

		// Only old/outdated should now be left. Delete them.
		for(AreaMarker marker : markers.values()) {
			marker.deleteMarker();
		}
	}

	// -------------------------------------------- //
	// UPDATE: PLAYERSET
	// -------------------------------------------- //

	// Thread Safe / Asynchronous: Yes
	public String createPlayersetId(Faction faction) {
		if(faction == null) {
			return null;
		}
		if(faction.isWilderness()) {
			return null;
		}
		return FACTIONS_PLAYERSET_ + faction.getId();
	}

	// Thread Safe / Asynchronous: Yes
	public Set<String> createPlayerset(Faction faction) {
		if(faction == null) {
			return null;
		}
		if(faction.isWilderness()) {
			return null;
		}

		Set<String> ret = new HashSet<>();

		for(FPlayer fplayer : faction.getFPlayers()) {
			// NOTE: We add both UUID and name. This might be a good idea for future proofing.
			ret.add(fplayer.getId());
			ret.add(fplayer.getName());
		}

		return ret;
	}

	// Thread Safe / Asynchronous: Yes
	public Map<String, Set<String>> createPlayersets() {
		if(!config.dynmap().isVisibilityByFaction()) {
			return null;
		}

		Map<String, Set<String>> ret = new HashMap<>();

		for(Faction faction : Factions.getInstance().getAllFactions()) {
			String playersetId = createPlayersetId(faction);
			if(playersetId == null) {
				continue;
			}
			Set<String> playerIds = createPlayerset(faction);
			if(playerIds == null) {
				continue;
			}
			ret.put(playersetId, playerIds);
		}

		return ret;
	}

	// Thread Safe / Asynchronous: No
	public void updatePlayersets(Map<String, Set<String>> playersets) {
		if(playersets == null) {
			return;
		}

		// Remove
		for(PlayerSet set : this.markerApi.getPlayerSets()) {
			if(!set.getSetID().startsWith(FACTIONS_PLAYERSET_)) {
				continue;
			}

			// (Null means remove all)
			if(playersets.containsKey(set.getSetID())) {
				continue;
			}

			set.deleteSet();
		}

		// Add / Update
		for(Entry<String, Set<String>> entry : playersets.entrySet()) {
			// Extract from Entry
			String setId = entry.getKey();
			Set<String> playerIds = entry.getValue();

			// Get Creatively
			PlayerSet set = this.markerApi.getPlayerSet(setId);
			if(set == null) {
				set = this.markerApi.createPlayerSet(setId, // id
						true, // symmetric
						playerIds, // players
						false // persistent
				);
			}
			if(set == null) {
				logSevere("Could not get/create the player set " + setId);
				continue;
			}

			// Set Content
			set.setPlayers(playerIds);
		}
	}

	// -------------------------------------------- //
	// UTIL & SHARED
	// -------------------------------------------- //

	// Thread Safe / Asynchronous: Yes
	private String getDescription(Faction faction) {
		String ret = "<div class=\"regioninfo\">" + config.dynmap().getDescription() + "</div>";

		// Name
		String name = faction.getTag();
		name = ChatColor.stripColor(name);
		name = escapeHtml(name);
		ret = ret.replace("%name%", name);

		// Description
		String description = faction.getDescription();
		description = ChatColor.stripColor(description);
		description = escapeHtml(description);
		ret = ret.replace("%description%", description);

		// Players
		Set<FPlayer> playersList = faction.getFPlayers();
		String playersCount = String.valueOf(playersList.size());
		String players = getHtmlPlayerString(playersList);

		FPlayer playersLeaderObject = faction.getFPlayerAdmin();
		String playersLeader = getHtmlPlayerName(playersLeaderObject);

		List<FPlayer> playersAdminsList = faction.getFPlayersWhereRole(Role.ADMIN);
		String playersAdminsCount = String.valueOf(playersAdminsList.size());
		String playersAdmins = getHtmlPlayerString(playersAdminsList);

		List<FPlayer> playersModeratorsList = faction.getFPlayersWhereRole(Role.MODERATOR);
		String playersModeratorsCount = String.valueOf(playersModeratorsList.size());
		String playersModerators = getHtmlPlayerString(playersModeratorsList);


		List<FPlayer> playersNormalsList = faction.getFPlayersWhereRole(Role.NORMAL);
		String playersNormalsCount = String.valueOf(playersNormalsList.size());
		String playersNormals = getHtmlPlayerString(playersNormalsList);


		ret = ret.replace("%players%", players);
		ret = ret.replace("%players.count%", playersCount);
		ret = ret.replace("%players.leader%", playersLeader);
		ret = ret.replace("%players.admins%", playersAdmins);
		ret = ret.replace("%players.admins.count%", playersAdminsCount);
		ret = ret.replace("%players.moderators%", playersModerators);
		ret = ret.replace("%players.moderators.count%", playersModeratorsCount);
		ret = ret.replace("%players.normals%", playersNormals);
		ret = ret.replace("%players.normals.count%", playersNormalsCount);

		return ret;
	}

	public static String getHtmlPlayerString(Collection<FPlayer> playersOfficersList) {
		StringBuilder ret = new StringBuilder();
		for(FPlayer fplayer : playersOfficersList) {
			if(ret.length() > 0) {
				ret.append(", ");
			}
			ret.append(getHtmlPlayerName(fplayer));
		}
		return ret.toString();
	}

	public static String getHtmlPlayerName(FPlayer fplayer) {
		if(fplayer == null) {
			return "none";
		}
		return escapeHtml(fplayer.getName());
	}

	public static String escapeHtml(String string) {
		if(string == null) {
			return "";
		}
		StringBuilder out = new StringBuilder(Math.max(16, string.length()));
		for(int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if(c > 127 || c == '"' || c == '<' || c == '>' || c == '&') {
				out.append("&#");
				out.append((int) c);
				out.append(';');
			} else {
				out.append(c);
			}
		}
		return out.toString();
	}

	// Thread Safe / Asynchronous: Yes
	private boolean isVisible(Faction faction) {
		if(faction == null) {
			return false;
		}
		final int factionId = faction.getId();
		final String factionName = faction.getTag();
		if(factionName == null) {
			return false;
		}

		Set<Integer> visible = config.dynmap().getVisibleFactions();
		Set<Integer> hidden = config.dynmap().getHiddenFactions();

		if(!visible.isEmpty() && !visible.contains(factionId)) {
			return false;
		}

		return !hidden.contains(factionId);
	}

	// Thread Safe / Asynchronous: Yes
	public DynMapStyle getStyle(Faction faction) {
		DynMapStyle ret;

		ret = config.dynmap().getFactionStyles().get(faction.getId());
		if(ret != null) {
			return ret;
		}

		return DynMapStyle.getDefault();
	}

	// Thread Safe / Asynchronous: Yes
	public static void logInfo(String msg) {
		String message = DYNMAP_INTEGRATION + msg;
		System.out.println(message);
	}

	// Thread Safe / Asynchronous: Yes
	public static void logSevere(String msg) {
		String message = DYNMAP_INTEGRATION + ChatColor.RED + msg;
		System.out.println(message);
	}

	enum Direction {
		XPLUS, ZPLUS, XMINUS, ZMINUS
	}

	// Find all contiguous blocks, set in target and clear in source
	private void floodFillTarget(TileFlags source, TileFlags destination, int x, int y) {
		ArrayDeque<int[]> stack = new ArrayDeque<>();
		stack.push(new int[] {x, y});

		while(!stack.isEmpty()) {
			int[] nxt = stack.pop();
			x = nxt[0];
			y = nxt[1];
			if(source.getFlag(x, y)) { // Set in src
				source.setFlag(x, y, false); // Clear source
				destination.setFlag(x, y, true); // Set in destination
				if(source.getFlag(x + 1, y)) {
					stack.push(new int[] {x + 1, y});
				}
				if(source.getFlag(x - 1, y)) {
					stack.push(new int[] {x - 1, y});
				}
				if(source.getFlag(x, y + 1)) {
					stack.push(new int[] {x, y + 1});
				}
				if(source.getFlag(x, y - 1)) {
					stack.push(new int[] {x, y - 1});
				}
			}
		}
	}
}
