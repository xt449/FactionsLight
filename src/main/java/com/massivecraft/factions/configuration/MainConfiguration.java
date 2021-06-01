package com.massivecraft.factions.configuration;

import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.util.MaterialHelper;
import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.*;

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
		commands.show.exempt = config.getStringList("commands.show.exempt");
		//// Stuck:
		commands.stuck.cooldown = config.getInt("commands.stuck.cooldown");
		commands.stuck.delay = config.getInt("commands.stuck.delay");
		commands.stuck.radius = config.getInt("commands.stuck.radius");
		//// ToolTips:
		commands.toolTips.faction = config.getString("commands.toolTips.faction");
		commands.toolTips.player = config.getString("commands.toolTips.player");

		// Factions:
		//// LandRaidControl: TODO
		//// Prefix:
		factions.prefixes.admin = config.getString("factions.prefixes.admin");
		factions.prefixes.coleader = config.getString("factions.prefixes.coleader");
		factions.prefixes.mod = config.getString("factions.prefixes.mod");
		factions.prefixes.normal = config.getString("factions.prefixes.normal");
		factions.prefixes.recruit = config.getString("factions.prefixes.recruit");
		//// Chat:

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

		private transient Factions factions = new Factions();
		private transient Relations relations = new Relations();

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
			private transient java.util.List<String> exempt;

			public boolean minimal() {
				return minimal;
			}
			public String format() {
				return format;
			}
			public java.util.List<String> exempt() {
				return Collections.unmodifiableList(exempt);
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
//		public static class LandRaidControl {
//			public static class DTR {
//				private transient double startingDTR;
//				private transient double maxDTR;
//				private transient double minDTR;
//				private transient double perPlayer;
//				private transient double regainPerMinutePerPlayer;
//				private transient double regainPerMinuteMaxRate;
//				private transient double lossPerDeath;
//				private transient int freezeTime;
//				private transient boolean freezePreventsJoin;
//				private transient boolean freezePreventsLeave;
//				private transient boolean freezePreventsDisband;
//				private transient double freezeKickPenalty;
//				private transient String freezeTimeFormat;
//				private transient int landPerPlayer;
//				private transient int landStarting;
//				private transient int decimalDigits;
//				private transient Map<String, Double> worldDeathModifiers;
//
//				public int getDecimalDigits() {
//					return decimalDigits;
//				}
//
//				public int getLandPerPlayer() {
//					return landPerPlayer;
//				}
//
//				public int getLandStarting() {
//					return landStarting;
//				}
//
//				public int getFreezeTime() {
//					return freezeTime;
//				}
//
//				public String getFreezeTimeFormat() {
//					return freezeTimeFormat;
//				}
//
//				public boolean isFreezePreventsJoin() {
//					return freezePreventsJoin;
//				}
//
//				public boolean isFreezePreventsLeave() {
//					return freezePreventsLeave;
//				}
//
//				public boolean isFreezePreventsDisband() {
//					return freezePreventsDisband;
//				}
//
//				public double getFreezeKickPenalty() {
//					return freezeKickPenalty;
//				}
//
//				public double getMinDTR() {
//					return minDTR;
//				}
//
//				public double getPerPlayer() {
//					return perPlayer;
//				}
//
//				public double getRegainPerMinutePerPlayer() {
//					return regainPerMinutePerPlayer;
//				}
//
//				public double getRegainPerMinuteMaxRate() {
//					return regainPerMinuteMaxRate;
//				}
//
//				public double getMaxDTR() {
//					return maxDTR;
//				}
//
//				public double getStartingDTR() {
//					return startingDTR;
//				}
//
//				/**
//				 * Not used directly by the plugin, as it uses the helper method.
//				 *
//				 * @return loss per death
//				 * @see #getLossPerDeath(World)
//				 */
//				public double getLossPerDeathBase() {
//					return this.lossPerDeath;
//				}
//
//				public double getLossPerDeath(World world) {
//					if(this.worldDeathModifiers == null) {
//						this.worldDeathModifiers = new HashMap<>();
//					}
//					return this.lossPerDeath * this.worldDeathModifiers.getOrDefault(world.getName(), 1D);
//				}
//			}
//
//			public static class Power {
//				private transient double playerMin;
//				private transient double playerMax;
//				private transient double playerStarting;
//				private transient double powerPerMinute;
//				private transient double lossPerDeath;
//				private transient boolean regenOffline;
//				private transient double offlineLossPerDay;
//				private transient double offlineLossLimit;
//				private transient double factionMax;
//				private transient boolean respawnHomeFromNoPowerLossWorlds;
//				private transient Set<String> worldsNoPowerLoss;
//				private transient boolean peacefulMembersDisablePowerLoss;
//				private transient boolean warZonePowerLoss;
//				private transient boolean wildernessPowerLoss;
//				private transient boolean canLeaveWithNegativePower;
//				private transient boolean raidability;
//				private transient boolean raidabilityOnEqualLandAndPower;
//				private transient int powerFreeze;
//				private transient double vampirism;
//
//				public boolean isRaidability() {
//					return raidability;
//				}
//
//				public boolean isRaidabilityOnEqualLandAndPower() {
//					return raidabilityOnEqualLandAndPower;
//				}
//
//				public int getPowerFreeze() {
//					return powerFreeze;
//				}
//
//				public boolean canLeaveWithNegativePower() {
//					return canLeaveWithNegativePower;
//				}
//
//				public boolean isWarZonePowerLoss() {
//					return warZonePowerLoss;
//				}
//
//				public boolean isWildernessPowerLoss() {
//					return wildernessPowerLoss;
//				}
//
//				public double getPlayerMin() {
//					return playerMin;
//				}
//
//				public double getPlayerMax() {
//					return playerMax;
//				}
//
//				public double getPlayerStarting() {
//					return playerStarting;
//				}
//
//				public double getPowerPerMinute() {
//					return powerPerMinute;
//				}
//
//				public double getLossPerDeath() {
//					return lossPerDeath;
//				}
//
//				public boolean isRegenOffline() {
//					return regenOffline;
//				}
//
//				public double getOfflineLossPerDay() {
//					return offlineLossPerDay;
//				}
//
//				public double getOfflineLossLimit() {
//					return offlineLossLimit;
//				}
//
//				public double getFactionMax() {
//					return factionMax;
//				}
//
//				public boolean isRespawnHomeFromNoPowerLossWorlds() {
//					return respawnHomeFromNoPowerLossWorlds;
//				}
//
//				public Set<String> getWorldsNoPowerLoss() {
//					return Collections.unmodifiableSet(worldsNoPowerLoss);
//				}
//
//				public boolean isPeacefulMembersDisablePowerLoss() {
//					return peacefulMembersDisablePowerLoss;
//				}
//
//				public double getVampirism() {
//					return vampirism;
//				}
//			}
//
//			private transient String system;
//			private transient DTR dtr = new DTR();
//			private transient Power power = new Power();
//
//			public String getSystem() {
//				return system;
//			}
//
//			public DTR dtr() {
//				return this.dtr;
//			}
//
//			public Power power() {
//				return power;
//			}
//		}

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

		public static class PVP {
			private transient boolean disablePVPBetweenNeutralFactions;
			private transient boolean disablePVPForFactionlessPlayers;
			private transient boolean enablePVPAgainstFactionlessInAttackersLand;
			private transient boolean disablePeacefulPVPInWarzone;
			private transient int noPVPDamageToOthersForXSecondsAfterLogin;
			private transient Set<String> worldsIgnorePvP;

			public boolean isDisablePVPBetweenNeutralFactions() {
				return disablePVPBetweenNeutralFactions;
			}
			public boolean isDisablePVPForFactionlessPlayers() {
				return disablePVPForFactionlessPlayers;
			}
			public boolean isDisablePeacefulPVPInWarzone() {
				return disablePeacefulPVPInWarzone;
			}
			public boolean isEnablePVPAgainstFactionlessInAttackersLand() {
				return enablePVPAgainstFactionlessInAttackersLand;
			}
			public int getNoPVPDamageToOthersForXSecondsAfterLogin() {
				return noPVPDamageToOthersForXSecondsAfterLogin;
			}
			public Set<String> getWorldsIgnorePvP() {
				return Collections.unmodifiableSet(worldsIgnorePvP);
			}
		}

		public static class SpecialCase {
			private transient boolean peacefulTerritoryDisablePVP;
			private transient boolean peacefulTerritoryDisableMonsters;
			private transient boolean peacefulTerritoryDisableBoom;
			private transient boolean permanentFactionsDisableLeaderPromotion;
			private transient Set<String> ignoreBuildMaterials;
			private transient Set<Material> ignoreBuildMaterialsMat;

			public Set<Material> getIgnoreBuildMaterials() {
				if(ignoreBuildMaterialsMat == null) {
					ignoreBuildMaterialsMat = new HashSet<>();
					ignoreBuildMaterials.forEach(m -> ignoreBuildMaterialsMat.add(MaterialHelper.getMaterial(m)));
					ignoreBuildMaterialsMat.remove(Material.AIR);
					ignoreBuildMaterials = Collections.unmodifiableSet(ignoreBuildMaterials);
				}
				return ignoreBuildMaterialsMat;
			}
			public boolean isPeacefulTerritoryDisablePVP() {
				return peacefulTerritoryDisablePVP;
			}
			public boolean isPeacefulTerritoryDisableMonsters() {
				return peacefulTerritoryDisableMonsters;
			}
			public boolean isPeacefulTerritoryDisableBoom() {
				return peacefulTerritoryDisableBoom;
			}
			public boolean isPermanentFactionsDisableLeaderPromotion() {
				return permanentFactionsDisableLeaderPromotion;
			}
		}

		public static class Claims {
			private transient boolean mustBeConnected;
			private transient boolean canBeUnconnectedIfOwnedByOtherFaction;
			private transient int requireMinFactionMembers;
			private transient int radiusClaimFailureLimit;
			private transient Set<String> worldsNoClaiming;
			private transient int bufferZone;
			private transient boolean allowOverClaim;
			private transient boolean allowOverClaimIgnoringBuffer;

			public boolean isAllowOverClaim() {
				return allowOverClaim;
			}
			public boolean isAllowOverClaimAndIgnoringBuffer() {
				return allowOverClaim && allowOverClaimIgnoringBuffer;
			}
			public int getBufferZone() {
				return bufferZone;
			}
			public boolean isMustBeConnected() {
				return mustBeConnected;
			}
			public boolean isCanBeUnconnectedIfOwnedByOtherFaction() {
				return canBeUnconnectedIfOwnedByOtherFaction;
			}
			public int getRequireMinFactionMembers() {
				return requireMinFactionMembers;
			}
			public int getRadiusClaimFailureLimit() {
				return radiusClaimFailureLimit;
			}
			public Set<String> getWorldsNoClaiming() {
				return Collections.unmodifiableSet(worldsNoClaiming);
			}
		}

		public static class Protection {
			private transient Set<String> permanentFactionMemberDenyCommands;
			private transient Set<String> territoryNeutralDenyCommands;
			private transient Set<String> territoryEnemyDenyCommands;
			private transient Set<String> territoryAllyDenyCommands;
			private transient Set<String> warzoneDenyCommands;
			private transient Set<String> wildernessDenyCommands;
			private transient boolean territoryBlockCreepers;
			private transient boolean territoryBlockCreepersWhenOffline;
			private transient boolean territoryBlockFireballs;
			private transient boolean territoryBlockFireballsWhenOffline;
			private transient boolean territoryBlockTNT;
			private transient boolean territoryBlockTNTWhenOffline;
			private transient boolean territoryBlockOtherExplosions;
			private transient boolean territoryBlockOtherExplosionsWhenOffline;
			private transient boolean territoryDenyEndermanBlocks;
			private transient boolean territoryDenyEndermanBlocksWhenOffline;
			private transient boolean territoryBlockEntityDamageMatchingPerms;
			private transient boolean safeZoneDenyBuild;
			private transient boolean safeZoneDenyUsage;
			private transient boolean safeZoneBlockTNT;
			private transient boolean safeZoneBlockOtherExplosions;
			private transient boolean safeZonePreventAllDamageToPlayers;
			private transient boolean safeZoneDenyEndermanBlocks;
			private transient boolean safeZoneBlockAllEntityDamage;
			private transient boolean peacefulBlockAllEntityDamage;
			private transient boolean warZoneDenyBuild;
			private transient boolean warZoneDenyUsage;
			private transient boolean warZoneBlockCreepers;
			private transient boolean warZoneBlockFireballs;
			private transient boolean warZoneBlockTNT;
			private transient boolean warZoneBlockOtherExplosions;
			private transient boolean warZoneFriendlyFire;
			private transient boolean warZoneDenyEndermanBlocks;
			private transient boolean wildernessDenyBuild;
			private transient boolean wildernessDenyUsage;
			private transient boolean wildernessBlockCreepers;
			private transient boolean wildernessBlockFireballs;
			private transient boolean wildernessBlockTNT;
			private transient boolean wildernessBlockOtherExplosions;
			private transient boolean wildernessDenyEndermanBlocks;
			private transient boolean pistonProtectionThroughDenyBuild;
			private transient Set<String> territoryDenyUsageMaterials;
			private transient Set<String> territoryDenyUsageMaterialsWhenOffline;
			private transient Set<Material> territoryDenyUsageMaterialsMat;
			private transient Set<Material> territoryDenyUsageMaterialsWhenOfflineMat;
			private transient Set<String> containerExceptions;
			private transient Set<Material> containerExceptionsMat;
			private transient Set<String> breakExceptions;
			private transient Set<Material> breakExceptionsMat;
			private transient Set<String> entityInteractExceptions;
			private transient Set<String> playersWhoBypassAllProtection;
			private transient Set<String> worldsNoWildernessProtection;

			public Set<String> getPermanentFactionMemberDenyCommands() {
				return Collections.unmodifiableSet(permanentFactionMemberDenyCommands);
			}
			public Set<String> getTerritoryNeutralDenyCommands() {
				return Collections.unmodifiableSet(territoryNeutralDenyCommands);
			}
			public Set<String> getTerritoryEnemyDenyCommands() {
				return Collections.unmodifiableSet(territoryEnemyDenyCommands);
			}
			public Set<String> getTerritoryAllyDenyCommands() {
				return Collections.unmodifiableSet(territoryAllyDenyCommands);
			}
			public Set<String> getWarzoneDenyCommands() {
				return Collections.unmodifiableSet(warzoneDenyCommands);
			}
			public Set<String> getWildernessDenyCommands() {
				return Collections.unmodifiableSet(wildernessDenyCommands);
			}
			public boolean isTerritoryBlockCreepers() {
				return territoryBlockCreepers;
			}
			public boolean isTerritoryBlockCreepersWhenOffline() {
				return territoryBlockCreepersWhenOffline;
			}
			public boolean isTerritoryBlockFireballs() {
				return territoryBlockFireballs;
			}
			public boolean isTerritoryBlockFireballsWhenOffline() {
				return territoryBlockFireballsWhenOffline;
			}
			public boolean isTerritoryBlockTNT() {
				return territoryBlockTNT;
			}
			public boolean isTerritoryBlockTNTWhenOffline() {
				return territoryBlockTNTWhenOffline;
			}
			public boolean isTerritoryDenyEndermanBlocks() {
				return territoryDenyEndermanBlocks;
			}
			public boolean isTerritoryDenyEndermanBlocksWhenOffline() {
				return territoryDenyEndermanBlocksWhenOffline;
			}
			public boolean isTerritoryBlockEntityDamageMatchingPerms() {
				return territoryBlockEntityDamageMatchingPerms;
			}
			public boolean isSafeZoneDenyBuild() {
				return safeZoneDenyBuild;
			}
			public boolean isSafeZoneDenyUsage() {
				return safeZoneDenyUsage;
			}
			public boolean isSafeZoneBlockTNT() {
				return safeZoneBlockTNT;
			}
			public boolean isSafeZonePreventAllDamageToPlayers() {
				return safeZonePreventAllDamageToPlayers;
			}
			public boolean isSafeZoneDenyEndermanBlocks() {
				return safeZoneDenyEndermanBlocks;
			}
			public boolean isSafeZoneBlockAllEntityDamage() {
				return safeZoneBlockAllEntityDamage;
			}
			public boolean isPeacefulBlockAllEntityDamage() {
				return peacefulBlockAllEntityDamage;
			}
			public boolean isWarZoneDenyBuild() {
				return warZoneDenyBuild;
			}
			public boolean isWarZoneDenyUsage() {
				return warZoneDenyUsage;
			}
			public boolean isWarZoneBlockCreepers() {
				return warZoneBlockCreepers;
			}
			public boolean isWarZoneBlockFireballs() {
				return warZoneBlockFireballs;
			}
			public boolean isWarZoneBlockTNT() {
				return warZoneBlockTNT;
			}
			public boolean isWarZoneFriendlyFire() {
				return warZoneFriendlyFire;
			}
			public boolean isWarZoneDenyEndermanBlocks() {
				return warZoneDenyEndermanBlocks;
			}
			public boolean isWildernessDenyBuild() {
				return wildernessDenyBuild;
			}
			public boolean isWildernessDenyUsage() {
				return wildernessDenyUsage;
			}
			public boolean isWildernessBlockCreepers() {
				return wildernessBlockCreepers;
			}
			public boolean isWildernessBlockFireballs() {
				return wildernessBlockFireballs;
			}
			public boolean isWildernessBlockTNT() {
				return wildernessBlockTNT;
			}
			public boolean isWildernessDenyEndermanBlocks() {
				return wildernessDenyEndermanBlocks;
			}
			public boolean isPistonProtectionThroughDenyBuild() {
				return pistonProtectionThroughDenyBuild;
			}
			public boolean isTerritoryBlockOtherExplosions() {
				return territoryBlockOtherExplosions;
			}
			public boolean isTerritoryBlockOtherExplosionsWhenOffline() {
				return territoryBlockOtherExplosionsWhenOffline;
			}
			public boolean isSafeZoneBlockOtherExplosions() {
				return safeZoneBlockOtherExplosions;
			}
			public boolean isWarZoneBlockOtherExplosions() {
				return warZoneBlockOtherExplosions;
			}
			public boolean isWildernessBlockOtherExplosions() {
				return wildernessBlockOtherExplosions;
			}

			public Set<Material> getTerritoryDenyUsageMaterials() {
				if(territoryDenyUsageMaterialsMat == null) {
					territoryDenyUsageMaterialsMat = new HashSet<>();
					territoryDenyUsageMaterials.forEach(m -> territoryDenyUsageMaterialsMat.add(MaterialHelper.getMaterial(m)));
					territoryDenyUsageMaterialsMat.remove(Material.AIR);
					territoryDenyUsageMaterialsMat = Collections.unmodifiableSet(territoryDenyUsageMaterialsMat);
				}
				return territoryDenyUsageMaterialsMat;
			}

			public Set<Material> getTerritoryDenyUsageMaterialsWhenOffline() {
				if(territoryDenyUsageMaterialsWhenOfflineMat == null) {
					territoryDenyUsageMaterialsWhenOfflineMat = new HashSet<>();
					territoryDenyUsageMaterialsWhenOffline.forEach(m -> territoryDenyUsageMaterialsWhenOfflineMat.add(MaterialHelper.getMaterial(m)));
					territoryDenyUsageMaterialsWhenOfflineMat.remove(Material.AIR);
					territoryDenyUsageMaterialsWhenOfflineMat = Collections.unmodifiableSet(territoryDenyUsageMaterialsWhenOfflineMat);
				}
				return territoryDenyUsageMaterialsWhenOfflineMat;
			}

			public Set<Material> getContainerExceptions() {
				if(containerExceptionsMat == null) {
					containerExceptionsMat = new HashSet<>();
					containerExceptions.forEach(m -> containerExceptionsMat.add(MaterialHelper.getMaterial(m)));
					containerExceptionsMat.remove(Material.AIR);
					containerExceptionsMat = Collections.unmodifiableSet(containerExceptionsMat);
				}
				return containerExceptionsMat;
			}

			public Set<Material> getBreakExceptions() {
				if(breakExceptionsMat == null) {
					breakExceptionsMat = new HashSet<>();
					breakExceptions.forEach(m -> breakExceptionsMat.add(MaterialHelper.getMaterial(m)));
					breakExceptionsMat.remove(Material.AIR);
					breakExceptionsMat = Collections.unmodifiableSet(breakExceptionsMat);
				}
				return breakExceptionsMat;
			}

			public Set<String> getEntityInteractExceptions() {
				return Collections.unmodifiableSet(entityInteractExceptions);
			}

			public Set<String> getPlayersWhoBypassAllProtection() {
				return Collections.unmodifiableSet(playersWhoBypassAllProtection);
			}

			public Set<String> getWorldsNoWildernessProtection() {
				return Collections.unmodifiableSet(worldsNoWildernessProtection);
			}
		}

		public static class Spawning {
			private transient Set<String> preventSpawningInSafezone;
			private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInSafezoneReason;
			private transient Set<String> preventSpawningInSafezoneExceptions;
			private transient Set<EntityType> preventSpawningInSafezoneExceptionsType;
			private transient Set<String> preventSpawningInWarzone;
			private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInWarzoneReason;
			private transient Set<String> preventSpawningInWarzoneExceptions;
			private transient Set<EntityType> preventSpawningInWarzoneExceptionsType;
			private transient Set<String> preventSpawningInWilderness;
			private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInWildernessReason;
			private transient Set<String> preventSpawningInWildernessExceptions;
			private transient Set<EntityType> preventSpawningInWildernessExceptionsType;
			private transient Set<String> preventSpawningInTerritory;
			private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInTerritoryReason;
			private transient Set<String> preventSpawningInTerritoryExceptions;
			private transient Set<EntityType> preventSpawningInTerritoryExceptionsType;

			public Set<CreatureSpawnEvent.SpawnReason> getPreventInSafezone() {
				if(preventSpawningInSafezoneReason == null) {
					preventSpawningInSafezoneReason = MiscUtil.typeSetFromStringSet(preventSpawningInSafezone, MiscUtil.SPAWN_REASON_FUNCTION);
				}
				return preventSpawningInSafezoneReason;
			}

			public Set<EntityType> getPreventInSafezoneExceptions() {
				if(preventSpawningInSafezoneExceptionsType == null) {
					preventSpawningInSafezoneExceptionsType = MiscUtil.typeSetFromStringSet(preventSpawningInSafezoneExceptions, MiscUtil.ENTITY_TYPE_FUNCTION);
				}
				return preventSpawningInSafezoneExceptionsType;
			}

			public Set<CreatureSpawnEvent.SpawnReason> getPreventInTerritory() {
				if(preventSpawningInTerritoryReason == null) {
					preventSpawningInTerritoryReason = MiscUtil.typeSetFromStringSet(preventSpawningInTerritory, MiscUtil.SPAWN_REASON_FUNCTION);
				}
				return preventSpawningInTerritoryReason;
			}

			public Set<EntityType> getPreventInTerritoryExceptions() {
				if(preventSpawningInTerritoryExceptionsType == null) {
					preventSpawningInTerritoryExceptionsType = MiscUtil.typeSetFromStringSet(preventSpawningInTerritoryExceptions, MiscUtil.ENTITY_TYPE_FUNCTION);
				}
				return preventSpawningInTerritoryExceptionsType;
			}

			public Set<CreatureSpawnEvent.SpawnReason> getPreventInWarzone() {
				if(preventSpawningInWarzoneReason == null) {
					preventSpawningInWarzoneReason = MiscUtil.typeSetFromStringSet(preventSpawningInWarzone, MiscUtil.SPAWN_REASON_FUNCTION);
				}
				return preventSpawningInWarzoneReason;
			}

			public Set<EntityType> getPreventInWarzoneExceptions() {
				if(preventSpawningInWarzoneExceptionsType == null) {
					preventSpawningInWarzoneExceptionsType = MiscUtil.typeSetFromStringSet(preventSpawningInWarzoneExceptions, MiscUtil.ENTITY_TYPE_FUNCTION);
				}
				return preventSpawningInWarzoneExceptionsType;
			}

			public Set<CreatureSpawnEvent.SpawnReason> getPreventInWilderness() {
				if(preventSpawningInWildernessReason == null) {
					preventSpawningInWildernessReason = MiscUtil.typeSetFromStringSet(preventSpawningInWilderness, MiscUtil.SPAWN_REASON_FUNCTION);
				}
				return preventSpawningInWildernessReason;
			}

			public Set<EntityType> getPreventInWildernessExceptions() {
				if(preventSpawningInWildernessExceptionsType == null) {
					preventSpawningInWildernessExceptionsType = MiscUtil.typeSetFromStringSet(preventSpawningInWildernessExceptions, MiscUtil.ENTITY_TYPE_FUNCTION);
				}
				return preventSpawningInWildernessExceptionsType;
			}
		}

		public static class OwnedArea {
			private transient boolean enabled;
			private transient int limitPerFaction;
			private transient boolean moderatorsBypass;
			private transient boolean denyBuild;
			private transient boolean painBuild;
			private transient boolean protectMaterials;
			private transient boolean denyUsage;

			private transient boolean messageOnBorder;
			private transient boolean messageInsideTerritory;
			private transient boolean messageByChunk;

			public boolean isEnabled() {
				return enabled;
			}

			public int getLimitPerFaction() {
				return limitPerFaction;
			}

			public boolean isModeratorsBypass() {
				return moderatorsBypass;
			}

			public boolean isDenyBuild() {
				return denyBuild;
			}

			public boolean isPainBuild() {
				return painBuild;
			}

			public boolean isProtectMaterials() {
				return protectMaterials;
			}

			public boolean isDenyUsage() {
				return denyUsage;
			}

			public boolean isMessageOnBorder() {
				return messageOnBorder;
			}

			public boolean isMessageInsideTerritory() {
				return messageInsideTerritory;
			}

			public boolean isMessageByChunk() {
				return messageByChunk;
			}
		}

		public static class Other {
			private transient boolean allowMultipleColeaders;
			private transient int tagLengthMin;
			private transient int tagLengthMax;
			private transient boolean tagForceUpperCase;

			private transient boolean newFactionsDefaultOpen;
			private transient boolean newFactionsDefaultPeaceful;
			private transient int factionMemberLimit;
			private transient String newPlayerStartingFactionID;

			private transient double saveToFileEveryXMinutes;

			private transient double autoLeaveAfterDaysOfInactivity;
			private transient double autoLeaveRoutineRunsEveryXMinutes;
			private transient int autoLeaveRoutineMaxMillisecondsPerTick;
			private transient boolean removePlayerDataWhenBanned;
			private transient boolean autoLeaveDeleteFPlayerData; // Let them just remove player from Faction.
			private transient double considerFactionsReallyOfflineAfterXMinutes;
			private transient int actionDeniedPainAmount;
			private transient String defaultRelation;
			private transient String defaultRole;
			private transient Role defaultRoleRole;
			private transient boolean disablePistonsInTerritory;
			private transient List<String> nameBlacklist;

			public List<String> getNameBlacklist() {
				return Collections.unmodifiableList(this.nameBlacklist);
			}

			public boolean isDisablePistonsInTerritory() {
				return disablePistonsInTerritory;
			}

			public String getDefaultRelation() {
				return defaultRelation;
			}

			public Role getDefaultRole() {
				if(defaultRoleRole == null) {
					if((defaultRoleRole = Role.fromString(defaultRole)) == null) {
						defaultRoleRole = Role.NORMAL;
					}
				}
				return defaultRoleRole;
			}

			public boolean isAllowMultipleColeaders() {
				return allowMultipleColeaders;
			}

			public int getTagLengthMin() {
				return tagLengthMin;
			}

			public int getTagLengthMax() {
				return tagLengthMax;
			}

			public boolean isTagForceUpperCase() {
				return tagForceUpperCase;
			}

			public boolean isNewFactionsDefaultOpen() {
				return newFactionsDefaultOpen;
			}

			public boolean isNewFactionsDefaultPeaceful() {
				return newFactionsDefaultPeaceful;
			}

			public int getFactionMemberLimit() {
				return factionMemberLimit;
			}

			public String getNewPlayerStartingFactionID() {
				return newPlayerStartingFactionID;
			}

			public double getSaveToFileEveryXMinutes() {
				return saveToFileEveryXMinutes;
			}

			public double getAutoLeaveAfterDaysOfInactivity() {
				return autoLeaveAfterDaysOfInactivity;
			}

			public double getAutoLeaveRoutineRunsEveryXMinutes() {
				return autoLeaveRoutineRunsEveryXMinutes;
			}

			public int getAutoLeaveRoutineMaxMillisecondsPerTick() {
				return autoLeaveRoutineMaxMillisecondsPerTick;
			}

			public boolean isRemovePlayerDataWhenBanned() {
				return removePlayerDataWhenBanned;
			}

			public boolean isAutoLeaveDeleteFPlayerData() {
				return autoLeaveDeleteFPlayerData;
			}

			public double getConsiderFactionsReallyOfflineAfterXMinutes() {
				return considerFactionsReallyOfflineAfterXMinutes;
			}

			public int getActionDeniedPainAmount() {
				return actionDeniedPainAmount;
			}
		}

		private transient PVP pvp = new PVP();
		private transient SpecialCase specialCase = new SpecialCase();
		private transient Claims claims = new Claims();
		private transient Protection protection = new Protection();
		private transient OwnedArea ownedArea = new OwnedArea();
		private transient Prefixes prefixes = new Prefixes();
//		private transient LandRaidControl landRaidControl = new LandRaidControl();
		private transient Other other = new Other();
		private transient Spawning spawning = new Spawning();

		public PVP pvp() {
			return pvp;
		}
		public SpecialCase specialCase() {
			return specialCase;
		}
		public Claims claims() {
			return claims;
		}
		public Protection protection() {
			return protection;
		}
		public Other other() {
			return other;
		}
		public OwnedArea ownedArea() {
			return ownedArea;
		}
		public Prefixes prefixes() {
			return prefixes;
		}
//		public LandRaidControl landRaidControl() {
//			return landRaidControl;
//		}
		public Spawning spawning() {
			return spawning;
		}
	}

	public static class Logging {
		private transient boolean factionCreate;
		private transient boolean factionDisband;
		private transient boolean factionJoin;
		private transient boolean factionKick;
		private transient boolean factionLeave;
		private transient boolean landClaims;
		private transient boolean landUnclaims;
		private transient boolean playerCommands;

		public boolean isFactionCreate() {
			return factionCreate;
		}
		public boolean isFactionDisband() {
			return factionDisband;
		}
		public boolean isFactionJoin() {
			return factionJoin;
		}
		public boolean isFactionKick() {
			return factionKick;
		}
		public boolean isFactionLeave() {
			return factionLeave;
		}
		public boolean isLandClaims() {
			return landClaims;
		}
		public boolean isLandUnclaims() {
			return landUnclaims;
		}
		public boolean isPlayerCommands() {
			return playerCommands;
		}
	}

	public static class MapSettings {
		private transient int width;
		private transient boolean showFactionKey;
		private transient boolean showNeutralFactionsOnMap;
		private transient boolean showEnemyFactions;
		private transient boolean showTruceFactions;

		public int getWidth() {
			return width;
		}
		public boolean isShowFactionKey() {
			return showFactionKey;
		}
		public boolean isShowNeutralFactionsOnMap() {
			return showNeutralFactionsOnMap;
		}
		public boolean isShowEnemyFactions() {
			return showEnemyFactions;
		}
		public boolean isShowTruceFactions() {
			return showTruceFactions;
		}
	}

	public static class RestrictWorlds {
		private transient boolean restrictWorlds;
		private transient boolean whitelist;
		private transient Set<String> worldList;

		public boolean isRestrictWorlds() {
			return restrictWorlds;
		}
		public boolean isWhitelist() {
			return whitelist;
		}
		public Set<String> getWorldList() {
			return Collections.unmodifiableSet(worldList);
		}
	}

	public static class Scoreboard {
		public static class Constant {
			private transient boolean enabled;
			private transient String title;
			private transient boolean prefixes;
			private transient String prefixTemplate;
			private transient boolean suffixes;
			private transient String suffixTemplate;
			private transient List<String> content;
			private transient boolean factionlessEnabled;
			private transient List<String> factionlessContent;
			private transient String factionlessTitle;

			public boolean isEnabled() {
				return enabled;
			}
			public String getTitle() {
				return title;
			}
			public boolean isPrefixes() {
				return prefixes;
			}
			public int getPrefixLength() {
				return 32;
			}
			public String getPrefixTemplate() {
				return prefixTemplate;
			}
			public boolean isSuffixes() {
				return suffixes;
			}
			public int getSuffixLength() {
				return 32;
			}
			public String getSuffixTemplate() {
				return suffixTemplate;
			}
			public List<String> getContent() {
				return Collections.unmodifiableList(content);
			}
			public boolean isFactionlessEnabled() {
				return factionlessEnabled;
			}
			public List<String> getFactionlessContent() {
				return Collections.unmodifiableList(factionlessContent);
			}
			public String getFactionlessTitle() {
				return factionlessTitle;
			}
		}

		public static class Info {
			private transient int expiration;
			private transient boolean enabled;
			private transient List<String> content;
			private transient String title;

			public int getExpiration() {
				return expiration;
			}
			public boolean isEnabled() {
				return enabled;
			}
			public List<String> getContent() {
				return Collections.unmodifiableList(content);
			}
			public String getTitle() {
				return title;
			}
		}

		private transient Constant constant = new Constant();
		private transient Info info = new Info();

		public Constant constant() {
			return constant;
		}
		public Info info() {
			return info;
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
		private transient boolean checking;
		private transient boolean buildPriority;

		public boolean isChecking() {
			return checking;
		}
		public boolean isBuildPriority() {
			return buildPriority;
		}
	}

	private transient Colors colors = new Colors();
	private transient Commands commands = new Commands();
	private transient Factions factions = new Factions();
	private transient Logging logging = new Logging();
	private transient MapSettings map = new MapSettings();
	private transient RestrictWorlds restrictWorlds = new RestrictWorlds();
	private transient Scoreboard scoreboard = new Scoreboard();
	private transient LWC lwc = new LWC();
	private transient WorldGuard worldGuard = new WorldGuard();

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
	public MapSettings map() {
		return map;
	}
	public RestrictWorlds restrictWorlds() {
		return restrictWorlds;
	}
	public Scoreboard scoreboard() {
		return scoreboard;
	}
	public WorldGuard worldGuard() {
		return worldGuard;
	}
	public LWC lwc() {
		return lwc;
	}
}
