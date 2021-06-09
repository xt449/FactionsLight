package com.massivecraft.factions.configuration;

import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

/**
 * @author Jonathan Talcott (xt449 / BinaryBanana)
 */
public class MainConfiguration extends AbstractConfiguration {

	public MainConfiguration(Plugin plugin) {
		super(plugin, "main.yml");
	}

	@Override
	protected void readValues() {
		// Colors:
		//// Relations:
		colors.relations.member = ChatColor.valueOf(config.getString("colors.relations.member"));
		colors.relations.ally = ChatColor.valueOf(config.getString("colors.relations.ally"));
		colors.relations.truce = ChatColor.valueOf(config.getString("colors.relations.truce"));
		colors.relations.neutral = ChatColor.valueOf(config.getString("colors.relations.neutral"));
		colors.relations.enemy = ChatColor.valueOf(config.getString("colors.relations.enemy"));
		colors.relations.peaceful = ChatColor.valueOf(config.getString("colors.relations.peaceful"));
		//// Factions:
		colors.factions.wilderness = ChatColor.valueOf(config.getString("colors.factions.wilderness"));
		colors.factions.safezone = ChatColor.valueOf(config.getString("colors.factions.safezone"));
		colors.factions.warzone = ChatColor.valueOf(config.getString("colors.factions.warzone"));

		// Commands:
		//// CommandPost:
		commands.commandPost.cooldown = config.getInt("commands.commandPost.cooldown");
		commands.commandPost.delay = config.getInt("commands.commandPost.delay");
		commands.commandPost.mustBeInClaimedTerritory = config.getBoolean("commands.commandPost.mustBeInClaimedTerritory");
		commands.commandPost.teleportAllowedFromEnemyTerritory = config.getBoolean("commands.commandPost.teleportAllowedFromEnemyTerritory");
		commands.commandPost.teleportAllowedFromDifferentWorld = config.getBoolean("commands.commandPost.teleportAllowedFromDifferentWorld");
		commands.commandPost.teleportIgnoreEnemiesIfInFriendlyTerritory = config.getBoolean("commands.commandPost.teleportIgnoreEnemiesIfInFriendlyTerritory");
		commands.commandPost.teleportAllowedEnemyDistance = config.getDouble("commands.commandPost.teleportAllowedEnemyDistance");
		//// List:
		commands.list.cooldown = config.getInt("commands.list.cooldown");
		commands.list.delay = config.getInt("commands.list.delay");
		commands.list.header = config.getString("commands.list.header");
		commands.list.footer = config.getString("commands.list.footer");
		commands.list.entry = config.getString("commands.list.entry");
		commands.list.factionlessEntry = config.getString("commands.list.factionlessEntry");
		//// Map:
		commands.map.cooldown = config.getInt("commands.map.cooldown");
		commands.map.delay = config.getInt("commands.map.delay");
		//// Perms:
		commands.perms.cooldown = config.getInt("commands.perms.cooldown");
		commands.perms.delay = config.getInt("commands.perms.delay");
		commands.perms.guiLore = config.getString("commands.perms.guiLore");
		//// Show:
		commands.show.cooldown = config.getInt("commands.show.cooldown");
		commands.show.delay = config.getInt("commands.show.delay");
		commands.show.minimal = config.getBoolean("commands.show.minimal");
		commands.show.format = config.getString("commands.show.format");
		//// Stuck:
		commands.stuck.cooldown = config.getInt("commands.stuck.cooldown");
		commands.stuck.delay = config.getInt("commands.stuck.delay");
		commands.stuck.radius = config.getInt("commands.stuck.radius");
		//// ToolTips:
		commands.toolTips.faction = config.getString("commands.toolTips.faction");
		commands.toolTips.player = config.getString("commands.toolTips.player");

		// Factions:
		//// Combat:
		factions.combat.gracePeriodOnLogin = config.getInt("factions.combat.gracePeriodOnLogin");
		factions.combat.gracePeriodOnRespawn = config.getInt("factions.combat.gracePeriodOnRespawn");
		factions.combat.allowByDefault = config.getBoolean("factions.combat.allowByDefault");
		//// ComandBlacklist:
		factions.commandBlacklist.inWilderness = Collections.unmodifiableList(config.getStringList("factions.commandBlacklist.inWilderness"));
		factions.commandBlacklist.inNeutralClaim = Collections.unmodifiableList(config.getStringList("factions.commandBlacklist.inNeutralClaim"));
		factions.commandBlacklist.inFriendlyClaim = Collections.unmodifiableList(config.getStringList("factions.commandBlacklist.inFriendlyClaim"));
		factions.commandBlacklist.inEnemyClaim = Collections.unmodifiableList(config.getStringList("factions.commandBlacklist.inEnemyClaim"));
		factions.commandBlacklist.inPermanentClaim = Collections.unmodifiableList(config.getStringList("factions.commandBlacklist.inPermanentClaim"));
		//// Limits:
		factions.limits.tagLengthMin = config.getInt("factions.limits.tagLengthMin");
		factions.limits.tagLengthMax = config.getInt("factions.limits.tagLengthMax");
		factions.limits.nameBlacklist = Collections.unmodifiableList(config.getStringList("factions.limits.nameBlacklist"));
		factions.limits.factionMemberLimit = config.getInt("factions.limits.factionMemberLimit");
		//// Roles:
		factions.roles.defaultRelation = Relation.valueOf(config.getString("factions.roles.defaultRelation"));
		factions.roles.defaultRole = Role.valueOf(config.getString("factions.roles.defaultRole"));
		//// Prefix:
		factions.prefixes.admin = config.getString("factions.prefixes.admin");
		factions.prefixes.coleader = config.getString("factions.prefixes.coleader");
		factions.prefixes.mod = config.getString("factions.prefixes.mod");
		factions.prefixes.normal = config.getString("factions.prefixes.normal");
		factions.prefixes.recruit = config.getString("factions.prefixes.recruit");

		// Logging:
		logging.factionCreate = config.getBoolean("logging.factionCreate");
		logging.factionDisband = config.getBoolean("logging.factionDisband");
		logging.factionJoin = config.getBoolean("logging.factionJoin");
		logging.factionKick = config.getBoolean("logging.factionKick");
		logging.factionLeave = config.getBoolean("logging.factionLeave");
		logging.landClaim = config.getBoolean("logging.landClaim");
		logging.landUnclaim = config.getBoolean("logging.landUnclaim");

		// RestrictWorlds:
		restrictWorlds.whitelist = config.getBoolean("restrictWorlds.whitelist");
		restrictWorlds.worldList = Collections.unmodifiableList(config.getStringList("restrictWorlds.worldList"));

		// LWC:
		lwc.enabled = config.getBoolean("lwc.enabled");
		lwc.resetLocksOnUnclaim = config.getBoolean("lwc.resetLocksOnUnclaim");
		lwc.resetLocksOnCapture = config.getBoolean("lwc.resetLocksOnCapture");

		// WorldGuard:
		worldGuard.enabled = config.getBoolean("worldGuard.enabled");
		worldGuard.buildPriority = config.getBoolean("worldGuard.buildPriority");
	}

	public static class Colors {
		public static class Relations {
			private transient ChatColor member;
			private transient ChatColor ally;
			private transient ChatColor truce;
			private transient ChatColor neutral;
			private transient ChatColor enemy;
			private transient ChatColor peaceful;

			public ChatColor member() {
				return member;
			}

			public ChatColor ally() {
				return ally;
			}

			public ChatColor truce() {
				return truce;
			}

			public ChatColor neutral() {
				return neutral;
			}

			public ChatColor enemy() {
				return enemy;
			}

			public ChatColor peaceful() {
				return peaceful;
			}
		}

		public static class Factions {
			private transient ChatColor wilderness;
			private transient ChatColor safezone;
			private transient ChatColor warzone;

			public ChatColor wilderness() {
				return wilderness;
			}

			public ChatColor safezone() {
				return safezone;
			}

			public ChatColor warzone() {
				return warzone;
			}
		}

		private final transient Factions factions = new Factions();
		private final transient Relations relations = new Relations();

		public Relations relations() {
			return relations;
		}

		public Factions factions() {
			return factions;
		}
	}

	public static class Commands {

		public static abstract class CommandConfiguration {
			protected transient int cooldown;
			protected transient int delay;

			public int cooldown() {
				return cooldown;
			}

			public int delay() {
				return delay;
			}
		}

		public static class CommandPost extends CommandConfiguration {
			private transient boolean mustBeInClaimedTerritory;
			private transient boolean teleportAllowedFromEnemyTerritory;
			private transient boolean teleportAllowedFromDifferentWorld;
			private transient boolean teleportIgnoreEnemiesIfInFriendlyTerritory;
			private transient double teleportAllowedEnemyDistance;

			public boolean isMustBeInClaimedTerritory() {
				return mustBeInClaimedTerritory;
			}

			public boolean isTeleportAllowedFromEnemyTerritory() {
				return teleportAllowedFromEnemyTerritory;
			}

			public boolean isTeleportAllowedFromDifferentWorld() {
				return teleportAllowedFromDifferentWorld;
			}

			public boolean isTeleportIgnoreEnemiesIfInFriendlyTerritory() {
				return teleportIgnoreEnemiesIfInFriendlyTerritory;
			}

			public double getTeleportAllowedEnemyDistance() {
				return teleportAllowedEnemyDistance;
			}
		}

		public static class List extends CommandConfiguration {
			private transient String header;
			private transient String footer;
			private transient String entry;
			private transient String factionlessEntry;

			public String header() {
				return header;
			}

			public String footer() {
				return footer;
			}

			public String entry() {
				return entry;
			}

			public String factionlessEntry() {
				return factionlessEntry;
			}
		}

		public static class Map extends CommandConfiguration {
		}

		public static class Perms extends CommandConfiguration {
			private transient String guiLore;

			public String guiLore() {
				return guiLore;
			}
		}

		public static class Show extends CommandConfiguration {
			private transient boolean minimal;
			private transient String format;

			public boolean minimal() {
				return minimal;
			}

			public String format() {
				return format;
			}
		}

		public static class Stuck extends CommandConfiguration {
			private transient int radius;

			public int radius() {
				return radius;
			}
		}

		public static class ToolTips {
			private transient String faction;
			private transient String player;

			public String faction() {
				return faction;
			}

			public String player() {
				return player;
			}
		}

		private final transient CommandPost commandPost = new CommandPost();
		private final transient List list = new List();
		private final transient Map map = new Map();
		private final transient Perms perms = new Perms();
		private final transient Show show = new Show();
		private final transient Stuck stuck = new Stuck();
		private final transient ToolTips toolTips = new ToolTips();

		public CommandPost commandPost() {
			return commandPost;
		}

		public List list() {
			return list;
		}

		public Map map() {
			return map;
		}

		public Perms perms() {
			return perms;
		}

		public Show show() {
			return show;
		}

		public Stuck stuck() {
			return stuck;
		}

		public ToolTips toolTips() {
			return toolTips;
		}
	}

	public static class Factions {
		public static class Combat {
			private transient int gracePeriodOnLogin;
			private transient int gracePeriodOnRespawn;
			private transient boolean allowByDefault;

			public int gracePeriodOnLogin() {
				return gracePeriodOnLogin;
			}

			public int gracePeriodOnRespawn() {
				return gracePeriodOnRespawn;
			}

			public boolean allowByDefault() {
				return allowByDefault;
			}
		}

		public static class CommandBlacklist {
			private transient List<String> inWilderness;
			private transient List<String> inNeutralClaim;
			private transient List<String> inFriendlyClaim;
			private transient List<String> inEnemyClaim;
			private transient List<String> inPermanentClaim;

			public List<String> getInWilderness() {
				return inWilderness;
			}

			public List<String> getInNeutralClaim() {
				return inNeutralClaim;
			}

			public List<String> getInFriendlyClaim() {
				return inFriendlyClaim;
			}

			public List<String> getInEnemyClaim() {
				return inEnemyClaim;
			}

			public List<String> getInPermanentClaim() {
				return inPermanentClaim;
			}
		}

		public static class Limits {
			private transient int tagLengthMin;
			private transient int tagLengthMax;
			private transient List<String> nameBlacklist;
			private transient int factionMemberLimit;

			public int getTagLengthMin() {
				return tagLengthMin;
			}

			public int getTagLengthMax() {
				return tagLengthMax;
			}

			public List<String> getNameBlacklist() {
				return nameBlacklist;
			}

			public int getFactionMemberLimit() {
				return factionMemberLimit;
			}
		}

		public static class Roles {
			private transient Relation defaultRelation;
			private transient Role defaultRole;

			public Relation defaultRelation() {
				return defaultRelation;
			}

			public Role defaultRole() {
				return defaultRole;
			}
		}

		public static class Prefixes {
			private transient String admin;
			private transient String coleader;
			private transient String mod;
			private transient String normal;
			private transient String recruit;

			public String admin() {
				return admin;
			}

			public String coleader() {
				return coleader;
			}

			public String mod() {
				return mod;
			}

			public String normal() {
				return normal;
			}

			public String recruit() {
				return recruit;
			}
		}

		private final transient Combat combat = new Combat();
		private final transient CommandBlacklist commandBlacklist = new CommandBlacklist();
		private final transient Limits limits = new Limits();
		private final transient Roles roles = new Roles();
		private final transient Prefixes prefixes = new Prefixes();

		public Combat combat() {
			return combat;
		}

		public CommandBlacklist commandBlacklist() {
			return commandBlacklist;
		}

		public Limits limits() {
			return limits;
		}

		public Roles roles() {
			return roles;
		}

		public Prefixes prefixes() {
			return prefixes;
		}
	}

	public static class Logging {
		private transient boolean factionCreate;
		private transient boolean factionDisband;
		private transient boolean factionJoin;
		private transient boolean factionKick;
		private transient boolean factionLeave;
		private transient boolean landClaim;
		private transient boolean landUnclaim;

		public boolean factionCreate() {
			return factionCreate;
		}

		public boolean factionDisband() {
			return factionDisband;
		}

		public boolean factionJoin() {
			return factionJoin;
		}

		public boolean factionKick() {
			return factionKick;
		}

		public boolean factionLeave() {
			return factionLeave;
		}

		public boolean landClaim() {
			return landClaim;
		}

		public boolean landUnclaim() {
			return landUnclaim;
		}
	}

	public static class RestrictWorlds {
		private transient boolean whitelist;
		private transient List<String> worldList;

		public boolean isEnabled(World world) {
			return whitelist == worldList.contains(world.getName());
		}
	}

	public static class LWC {
		private transient boolean enabled;
		private transient boolean resetLocksOnUnclaim;
		private transient boolean resetLocksOnCapture;

		public boolean isEnabled() {
			return enabled;
		}

		public boolean isResetLocksOnUnclaim() {
			return resetLocksOnUnclaim;
		}

		public boolean isResetLocksOnCapture() {
			return resetLocksOnCapture;
		}
	}

	public static class WorldGuard {
		private transient boolean enabled;
		private transient boolean buildPriority;

		public boolean isEnabled() {
			return enabled;
		}

		public boolean isBuildPriority() {
			return buildPriority;
		}
	}

	private final transient Colors colors = new Colors();
	private final transient Commands commands = new Commands();
	private final transient Factions factions = new Factions();
	private final transient Logging logging = new Logging();
	private final transient RestrictWorlds restrictWorlds = new RestrictWorlds();
	private final transient LWC lwc = new LWC();
	private final transient WorldGuard worldGuard = new WorldGuard();

	public Colors colors() {
		return colors;
	}

	public Commands commands() {
		return commands;
	}

	public Factions factions() {
		return factions;
	}

	public Logging logging() {
		return logging;
	}

	public RestrictWorlds restrictWorlds() {
		return restrictWorlds;
	}

	public WorldGuard worldGuard() {
		return worldGuard;
	}

	public LWC lwc() {
		return lwc;
	}
}
