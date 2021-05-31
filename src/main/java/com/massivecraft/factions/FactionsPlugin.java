package com.massivecraft.factions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.configuration.DefaultPermissionsConfiguration;
import com.massivecraft.factions.configuration.DynMapConfiguration;
import com.massivecraft.factions.configuration.MainConfiguration;
import com.massivecraft.factions.data.SaveTask;
import com.massivecraft.factions.integration.ClipPlaceholderAPIManager;
import com.massivecraft.factions.integration.IWorldguard;
import com.massivecraft.factions.integration.IntegrationManager;
import com.massivecraft.factions.integration.VaultPermission;
import com.massivecraft.factions.landraidcontrol.LandRaidControl;
import com.massivecraft.factions.listeners.*;
import com.massivecraft.factions.perms.Permissible;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.PermissionsMapTypeAdapter;
import com.massivecraft.factions.struct.ChatMode;
import com.massivecraft.factions.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class FactionsPlugin extends JavaPlugin implements FactionsAPI {

	// Our single plugin instance.
	// Single 4 life.
	private static FactionsPlugin instance;

	public static FactionsPlugin getInstance() {
		return instance;
	}

	// Configurations
	public final MainConfiguration configMain = new MainConfiguration(this);
	public final DefaultPermissionsConfiguration configDefaultPermissions = new DefaultPermissionsConfiguration(this);
	public final DynMapConfiguration configDynMap = new DynMapConfiguration(this);

	private Integer saveTask = null;
	private boolean autoSave = true;
	private boolean loadSuccessful = false;

	// Some utils
	private Persist persist;
	private TextUtil txt;
	private WorldUtil worldUtil;

	public TextUtil txt() {
		return txt;
	}

	public WorldUtil worldUtil() {
		return worldUtil;
	}

	private PermUtil permUtil;

	// Persist related
	public final Gson gson = getGsonBuilder().create();

	// holds f stuck start times
	public final Map<UUID, Long> timers = new HashMap<>();

	//holds f stuck taskids
	public final Map<UUID, Integer> stuckMap = new HashMap<>();

	// Persistence related
	private boolean locked = false;

	private Integer autoLeaveTask = null;

	private ClipPlaceholderAPIManager clipPlaceholderAPIManager;
	private final Set<String> pluginsHandlingChat = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private IWorldguard worldguard;
	private LandRaidControl landRaidControl;

	private String startupLog;
	private String startupExceptionLog;
	private VaultPermission vaultPermission;
	public final boolean likesCats = Arrays.stream(FactionsPlugin.class.getDeclaredMethods()).anyMatch(m -> m.isSynthetic() && m.getName().startsWith("loadCon") && m.getName().endsWith("0"));

	public FactionsPlugin() {
		instance = this;
	}

	@Override
	public void onEnable() {
		this.loadSuccessful = false;
		StringBuilder startupBuilder = new StringBuilder();
		StringBuilder startupExceptionBuilder = new StringBuilder();
		Handler handler = new Handler() {
			@Override
			public void publish(LogRecord record) {
				if(record.getMessage() != null && record.getMessage().contains("Loaded class {0}")) {
					return;
				}
				startupBuilder.append('[').append(record.getLevel().getName()).append("] ").append(record.getMessage()).append('\n');
				if(record.getThrown() != null) {
					StringWriter stringWriter = new StringWriter();
					PrintWriter printWriter = new PrintWriter(stringWriter);
					record.getThrown().printStackTrace(printWriter);
					startupExceptionBuilder.append('[').append(record.getLevel().getName()).append("] ").append(record.getMessage()).append('\n')
							.append(stringWriter).append('\n');
				}
			}

			@Override
			public void flush() {

			}

			@Override
			public void close() throws SecurityException {

			}
		};
		getLogger().addHandler(handler);
		getLogger().info("=== Starting up! ===");
		long timeEnableStart = System.currentTimeMillis();

		getLogger().info("");
		getLogger().info("Patriam Factions UUID!");
		getLogger().info("Version " + this.getDescription().getVersion());
		getLogger().info("");
		getLogger().info("Need support? https://factions.support/help/");
		getLogger().info("");

		// Ensure data folder exists!
		this.getDataFolder().mkdirs();

		loadLang();

		// Load Configurations
		configMain.initialize();
		configDefaultPermissions.initialize();
		configDynMap.initialize();

		this.landRaidControl = LandRaidControl.getByName(configMain.factions().landRaidControl().getSystem());

		File dataFolder = new File(this.getDataFolder(), "data");
		if(!dataFolder.exists()) {
			dataFolder.mkdir();
		}

		// Create Utility Instances
		this.permUtil = new PermUtil(this);
		this.persist = new Persist(this);
		this.worldUtil = new WorldUtil(this);

		// attempt to get first command defined in plugin.yml as reference command, if any commands are defined in there
		// reference command will be used to prevent "unknown command" console messages
		String refCommand = "";
		try {
			Map<String, Map<String, Object>> refCmd = this.getDescription().getCommands();
			if(!refCmd.isEmpty()) {
				refCommand = (String) (refCmd.keySet().toArray()[0]);
			}
		} catch(ClassCastException ignored) {
		}

		// Register recurring tasks
		if(saveTask == null && configMain.factions().other().getSaveToFileEveryXMinutes() > 0.0) {
			long saveTicks = (long) (20 * 60 * configMain.factions().other().getSaveToFileEveryXMinutes()); // Approximately every 30 min by default
			saveTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SaveTask(this), saveTicks, saveTicks);
		}

		int loadedPlayers = FPlayers.getInstance().load();
		int loadedFactions = Factions.getInstance().load();
		for(FPlayer fPlayer : FPlayers.getInstance().getAllFPlayers()) {
			Faction faction = Factions.getInstance().getFactionById(fPlayer.getFactionId());
			if(faction == null) {
				log("Invalid faction id on " + fPlayer.getName() + ":" + fPlayer.getFactionId());
				fPlayer.resetFactionData();
				continue;
			}
			faction.addFPlayer(fPlayer);
		}
		int loadedClaims = Board.getInstance().load();
		Board.getInstance().clean();
		FactionsPlugin.getInstance().getLogger().info("Loaded " + loadedPlayers + " players in " + loadedFactions + " factions with " + loadedClaims + " claims");

		// Add Base Commands
		FCmdRoot cmdBase = new FCmdRoot();

		Plugin lwc = getServer().getPluginManager().getPlugin("LWC");
		if(lwc != null && lwc.getDescription().getWebsite() != null && !lwc.getDescription().getWebsite().contains("extended")) {
			getLogger().info(" ");
			getLogger().warning("Notice: LWC Extended is the updated, and best supported, continuation of LWC. https://www.spigotmc.org/resources/lwc-extended.69551/");
			getLogger().info(" ");
		}

		// start up task which runs the autoLeaveAfterDaysOfInactivity routine
		startAutoLeaveTask(false);

		// Register Event Handlers
		getServer().getPluginManager().registerEvents(new FactionsPlayerListener(this), this);
		getServer().getPluginManager().registerEvents(new FactionsChatListener(this), this);
		getServer().getPluginManager().registerEvents(new FactionsEntityListener(this), this);
		getServer().getPluginManager().registerEvents(new FactionsBlockListener(this), this);
		getServer().getPluginManager().registerEvents(new OneEightPlusListener(this), this);
		getServer().getPluginManager().registerEvents(new PortalListener(this), this);

		// since some other plugins execute commands directly through this command interface, provide it
		this.getCommand(refCommand).setExecutor(cmdBase);

		if(ChatColor.stripColor(TL.NOFACTION_PREFIX.toString()).equals("[4-]")) {
			getLogger().warning("Looks like you have an old, mistaken 'nofactions-prefix' in your lang.yml. It currently displays [4-] which is... strange.");
		}

		// Integration time
		getServer().getPluginManager().registerEvents(new IntegrationManager(this), this);

		new BukkitRunnable() {
			@Override
			public void run() {
				vaultPermission = new VaultPermission();
				cmdBase.done();
				getLogger().removeHandler(handler);
				startupLog = startupBuilder.toString();
				startupExceptionLog = startupExceptionBuilder.toString();
			}
		}.runTask(this);

		getLogger().info("=== Ready to go after " + (System.currentTimeMillis() - timeEnableStart) + "ms! ===");
		this.loadSuccessful = true;
	}

	private int intOr(String in, int or) {
		try {
			return Integer.parseInt(in);
		} catch(NumberFormatException ignored) {
			return or;
		}
	}

	public void setWorldGuard(IWorldguard wg) {
		this.worldguard = wg;
	}

	public void loadLang() {
		File lang = new File(getDataFolder(), "lang.yml");
		OutputStream out = null;
		InputStream defLangStream = this.getResource("lang.yml");
		if(!lang.exists()) {
			try {
				getDataFolder().mkdir();
				lang.createNewFile();
				if(defLangStream != null) {
					out = new FileOutputStream(lang);
					int read;
					byte[] bytes = new byte[1024];

					while((read = defLangStream.read(bytes)) != -1) {
						out.write(bytes, 0, read);
					}
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new BufferedReader(new InputStreamReader(defLangStream)));
					TL.setFile(defConfig);
				}
			} catch(IOException e) {
				getLogger().log(Level.SEVERE, "[Factions] Couldn't create language file.", e);
				getLogger().severe("[Factions] This is a fatal error. Now disabling");
				this.setEnabled(false); // Without it loaded, we can't send them messages
			} finally {
				if(defLangStream != null) {
					try {
						defLangStream.close();
					} catch(IOException e) {
						FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to close resource", e);
					}
				}
				if(out != null) {
					try {
						out.close();
					} catch(IOException e) {
						FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to close output", e);
					}
				}
			}
		}

		YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
		for(TL item : TL.values()) {
			if(conf.getString(item.getPath()) == null) {
				conf.set(item.getPath(), item.getDefault());
			}
		}

		// Remove this here because I'm sick of dealing with bug reports due to bad decisions on my part.
		if(conf.getString(TL.COMMAND_SHOW_POWER.getPath(), "").contains("%5$s")) {
			conf.set(TL.COMMAND_SHOW_POWER.getPath(), TL.COMMAND_SHOW_POWER.getDefault());
			log(Level.INFO, "Removed errant format specifier from f show power.");
		}

		TL.setFile(conf);
		try {
			conf.save(lang);
		} catch(IOException e) {
			getLogger().log(Level.WARNING, "Factions: Report this stack trace to drtshock.");
			FactionsPlugin.getInstance().getLogger().log(Level.SEVERE, "Failed to save lang.yml", e);
		}
	}

	public String getStartupLog() {
		return this.startupLog;
	}

	public String getStartupExceptionLog() {
		return this.startupExceptionLog;
	}

	public PermUtil getPermUtil() {
		return permUtil;
	}

	// -------------------------------------------- //
	// LOGGING
	// -------------------------------------------- //
	public void log(String msg) {
		log(Level.INFO, msg);
	}

	public void log(String str, Object... args) {
		log(Level.INFO, TextUtil.parse(str, args));
	}

	public void log(Level level, String str, Object... args) {
		log(level, TextUtil.parse(str, args));
	}

	public void log(Level level, String msg) {
		this.getLogger().log(level, msg);
	}

	public boolean getLocked() {
		return this.locked;
	}

	public void setLocked(boolean val) {
		this.locked = val;
		this.setAutoSave(val);
	}

	public boolean getAutoSave() {
		return this.autoSave;
	}

	public void setAutoSave(boolean val) {
		this.autoSave = val;
	}

	public LandRaidControl getLandRaidControl() {
		return this.landRaidControl;
	}

	public IWorldguard getWorldguard() {
		return this.worldguard;
	}

	public void setupPlaceholderAPI() {
		this.clipPlaceholderAPIManager = new ClipPlaceholderAPIManager();
		if(this.clipPlaceholderAPIManager.register()) {
			getLogger().info("Successfully registered placeholders with PlaceholderAPI.");
		}
	}

	public boolean isClipPlaceholderAPIHooked() {
		return this.clipPlaceholderAPIManager != null;
	}

	private GsonBuilder getGsonBuilder() {
		Type mapFLocToStringSetType = new TypeToken<Map<FLocation, Set<String>>>() {
		}.getType();

		Type accessType = new TypeToken<Map<Permissible, Map<PermissibleAction, Boolean>>>() {
		}.getType();

		GsonBuilder builder = new GsonBuilder();

		return builder
				.disableHtmlEscaping()
				.enableComplexMapKeySerialization()
				.excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE)
				.registerTypeAdapter(accessType, new PermissionsMapTypeAdapter())
				.registerTypeAdapter(LazyLocation.class, new MyLocationTypeAdapter())
				.registerTypeAdapter(mapFLocToStringSetType, new MapFLocToStringSetTypeAdapter())
				.registerTypeAdapterFactory(EnumTypeAdapter.ENUM_FACTORY);
	}

	@Override
	public void onDisable() {
		if(autoLeaveTask != null) {
			this.getServer().getScheduler().cancelTask(autoLeaveTask);
			autoLeaveTask = null;
		}

		if(saveTask != null) {
			this.getServer().getScheduler().cancelTask(saveTask);
			saveTask = null;
		}
		// only save data if plugin actually loaded successfully
		if(loadSuccessful) {
			Factions.getInstance().forceSave();
			FPlayers.getInstance().forceSave();
			Board.getInstance().forceSave();
		}
		log("Disabled");
	}

	public void startAutoLeaveTask(boolean restartIfRunning) {
		if(autoLeaveTask != null) {
			if(!restartIfRunning) {
				return;
			}
			this.getServer().getScheduler().cancelTask(autoLeaveTask);
		}

		if(configMain.factions().other().getAutoLeaveRoutineRunsEveryXMinutes() > 0.0) {
			long ticks = (long) (20 * 60 * configMain.factions().other().getAutoLeaveRoutineRunsEveryXMinutes());
			autoLeaveTask = getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoLeaveTask(), ticks, ticks);
		}
	}

	// -------------------------------------------- //
	// Functions for other plugins to hook into
	// -------------------------------------------- //

	// This value will be updated whenever new hooks are added
	@Override
	public int getAPIVersion() {
		// Updated from 4 to 5 for version 0.5.0
		return 4;
	}

	// If another plugin is handling insertion of chat tags, this should be used to notify Factions
	@Override
	public void setHandlingChat(Plugin plugin, boolean handling) {
		if(plugin == null) {
			throw new IllegalArgumentException("Null plugin!");
		}
		if(plugin == this) {
			throw new IllegalArgumentException("Nice try, but this plugin isn't going to register itself!");
		}
		if(handling) {
			this.pluginsHandlingChat.add(plugin.getName());
		} else {
			this.pluginsHandlingChat.remove(plugin.getName());
		}
	}

	@Override
	public boolean isAnotherPluginHandlingChat() {
		return configMain.factions().chat().isTagHandledByAnotherPlugin() || !this.pluginsHandlingChat.isEmpty();
	}

	// Simply put, should this chat event be left for Factions to handle? For now, that means players with Faction Chat
	// enabled or use of the Factions f command without a slash; combination of isPlayerFactionChatting() and isFactionsCommand()

	@Override
	public boolean shouldLetFactionsHandleThisChat(AsyncPlayerChatEvent event) {
		return event != null && (isPlayerFactionChatting(event.getPlayer()) || isFactionsCommand(event.getMessage()));
	}

	// Does player have Faction Chat enabled? If so, chat plugins should preferably not do channels,
	// local chat, or anything else which targets individual recipients, so Faction Chat can be done
	@Override
	public boolean isPlayerFactionChatting(Player player) {
		if(player == null) {
			return false;
		}
		FPlayer me = FPlayers.getInstance().getByPlayer(player);

		return me != null && me.getChatMode().isAtLeast(ChatMode.ALLIANCE);
	}

	// Is this chat message actually a Factions command, and thus should be left alone by other plugins?
	// TODO: GET THIS BACK AND WORKING
	public boolean isFactionsCommand(String check) {
		return !(check == null || check.isEmpty()); //&& this.handleCommand(null, check, true);
	}

	// Get a player's faction tag (faction name), mainly for usage by chat plugins for local/channel chat
	@Override
	public String getPlayerFactionTag(Player player) {
		return getPlayerFactionTagRelation(player, null);
	}

	// Same as above, but with relation (enemy/neutral/ally) coloring potentially added to the tag
	@Override
	public String getPlayerFactionTagRelation(Player speaker, Player listener) {
		String tag = "~";

		if(speaker == null) {
			return tag;
		}

		FPlayer me = FPlayers.getInstance().getByPlayer(speaker);
		if(me == null) {
			return tag;
		}

		// if listener isn't set, or config option is disabled, give back uncolored tag
		if(listener == null || !configMain.factions().chat().isTagRelationColored()) {
			tag = me.getChatTag().trim();
		} else {
			FPlayer you = FPlayers.getInstance().getByPlayer(listener);
			if(you == null) {
				tag = me.getChatTag().trim();
			} else  // everything checks out, give the colored tag
			{
				tag = me.getChatTag(you).trim();
			}
		}
		if(tag.isEmpty()) {
			tag = "~";
		}

		return tag;
	}

	// Get a player's title within their faction, mainly for usage by chat plugins for local/channel chat
	@Override
	public String getPlayerTitle(Player player) {
		if(player == null) {
			return "";
		}

		FPlayer me = FPlayers.getInstance().getByPlayer(player);
		if(me == null) {
			return "";
		}

		return me.getTitle().trim();
	}

	// Get a list of all faction tags (names)
	@Override
	public Set<String> getFactionTags() {
		return Factions.getInstance().getFactionTags();
	}

	// Get a list of all players in the specified faction
	@Override
	public Set<String> getPlayersInFaction(String factionTag) {
		Set<String> players = new HashSet<>();
		Faction faction = Factions.getInstance().getByTag(factionTag);
		if(faction != null) {
			for(FPlayer fplayer : faction.getFPlayers()) {
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	// Get a list of all online players in the specified faction
	@Override
	public Set<String> getOnlinePlayersInFaction(String factionTag) {
		Set<String> players = new HashSet<>();
		Faction faction = Factions.getInstance().getByTag(factionTag);
		if(faction != null) {
			for(FPlayer fplayer : faction.getFPlayersWhereOnline(true)) {
				players.add(fplayer.getName());
			}
		}
		return players;
	}

	public String getPrimaryGroup(OfflinePlayer player) {
		return this.vaultPermission.getPrimaryGroup(player);
	}
}
