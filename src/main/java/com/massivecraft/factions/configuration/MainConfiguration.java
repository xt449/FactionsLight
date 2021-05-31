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

		private final Factions factions = new Factions();
		private final Relations relations = new Relations();

		public Relations relations() {
			return relations;
		}

		public Factions factions() {
			return factions;
		}
	}

	public static class Commands {
		public static class Kick {
			private final boolean allowKickInEnemyTerritory = false;

			public boolean isAllowKickInEnemyTerritory() {
				return allowKickInEnemyTerritory;
			}
		}

		public static class Home {
			private final int delay = 0;

			public int getDelay() {
				return delay;
			}
		}

		public static class ListCmd {
			private final String header = "&e&m----------&r&e[ &2Faction List &9{pagenumber}&e/&9{pagecount} &e]&m----------";
			private final String footer = "";
			private final String factionlessEntry = "<i>Factionless<i> {factionless} online";
			private final String entry = "<a>{faction-relation-color}{faction} <i>{online} / {members} online, <a>Land / Power / Maxpower: <i>{chunks}/{power}/{maxPower}";

			public String getHeader() {
				return header;
			}

			public String getFooter() {
				return footer;
			}

			public String getFactionlessEntry() {
				return factionlessEntry;
			}

			public String getEntry() {
				return entry;
			}
		}

		public static class MapCmd {
			private final int cooldown = 700;

			public int getCooldown() {
				return cooldown;
			}
		}

		public static class Perms {
			private final List<String> guiLore = new ArrayList<String>() {
				{
					this.add("&8Access: {action-access-color}{action-access}");
					this.add("&8{action-desc}");
					this.add("");
					this.add("&8Left click to &a&lAllow");
					this.add("&8Right click to &c&lDeny");
				}
			};

			public List<String> getGuiLore() {
				return Collections.unmodifiableList(guiLore);
			}
		}

		public static class Show {
			private final List<String> format = new ArrayList<String>() {
				{
					this.add("{header}");
					this.add("<a>Description: <i>{description}");
					this.add("<a>Joining: <i>{joining}    {peaceful}");
					this.add("<a>Land / Power / Maxpower: <i> {chunks}/{power}/{maxPower}");
					this.add("<a>Raidable: {raidable}");
					this.add("<a>Founded: <i>{create-date}");
					this.add("<a>This faction is permanent, remaining even with no members.'");
					this.add("<a>Land value: <i>{land-value} {land-refund}");
					this.add("<a>Bans: <i>{faction-bancount}");
					this.add("<a>Allies(<i>{allies}<a>/<i>{max-allies}<a>): {allies-list} ");
					this.add("<a>Online: (<i>{online}<a>/<i>{members}<a>): {online-list}");
					this.add("<a>Offline: (<i>{offline}<a>/<i>{members}<a>): {offline-list}");
				}
			};
			private final boolean minimal = false;
			private final List<String> exempt = new ArrayList<String>() {
				{
					this.add("put_faction_tag_here");
				}
			};

			public List<String> getFormat() {
				return Collections.unmodifiableList(format);
			}

			public boolean isMinimal() {
				return minimal;
			}

			public List<String> getExempt() {
				return Collections.unmodifiableList(exempt);
			}
		}

		public static class Stuck {
			private final int delay = 30;
			private final int radius = 10;

			public int getDelay() {
				return delay;
			}

			public int getRadius() {
				return radius;
			}
		}

		public static class Warp {
			private final int delay = 0;

			public int getDelay() {
				return delay;
			}
		}

		public static class ToolTips {
			private final List<String> faction = new ArrayList<String>() {
				{
					this.add("&6Leader: &f{leader}");
					this.add("&6Claimed: &f{chunks}");
					this.add("&6Raidable: &f{raidable}");
					this.add("&6Warps: &f{warps}");
					this.add("&6Power: &f{power}/{maxPower}");
					this.add("&6Members: &f{online}/{members}");
				}
			};
			private final List<String> player = new ArrayList<String>() {
				{
					this.add("&6Last Seen: &f{lastSeen}");
					this.add("&6Power: &f{player-power}");
					this.add("&6Rank: &f{group}");
				}
			};

			public List<String> faction() {
				return Collections.unmodifiableList(faction);
			}

			public List<String> player() {
				return Collections.unmodifiableList(player);
			}
		}

		private final Kick kick = new Kick();
		private final Home home = new Home();
		private final ListCmd list = new ListCmd();
		private final MapCmd map = new MapCmd();
		private final Perms perms = new Perms();
		private final Show show = new Show();
		private final Stuck stuck = new Stuck();
		private final ToolTips toolTips = new ToolTips();
		private final Warp warp = new Warp();

		public Kick kick() {
			return kick;
		}

		public Home home() {
			return home;
		}

		public ListCmd list() {
			return list;
		}

		public MapCmd map() {
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

		public Warp warp() {
			return warp;
		}
	}

	public static class Factions {
		public static class LandRaidControl {
			public static class DTR {
				private final double startingDTR = 2.0;
				private final double maxDTR = 10.0;
				private final double minDTR = -3.0;
				private final double perPlayer = 1;
				private final double regainPerMinutePerPlayer = 0.05;
				private final double regainPerMinuteMaxRate = 0.1;
				private final double lossPerDeath = 1;
				private final int freezeTime = 0;
				private final boolean freezePreventsJoin = true;
				private final boolean freezePreventsLeave = true;
				private final boolean freezePreventsDisband = true;
				private final double freezeKickPenalty = 0.5;
				private final String freezeTimeFormat = "H:mm:ss";
				private final int landPerPlayer = 3;
				private final int landStarting = 6;
				private final int decimalDigits = 2;
				private Map<String, Number> worldDeathModifiers = new HashMap<String, Number>() {
					{
						this.put("world_nether", 0.5D);
						this.put("world_the_end", 0.25D);
					}
				};

				public int getDecimalDigits() {
					return decimalDigits;
				}

				public int getLandPerPlayer() {
					return landPerPlayer;
				}

				public int getLandStarting() {
					return landStarting;
				}

				public int getFreezeTime() {
					return freezeTime;
				}

				public String getFreezeTimeFormat() {
					return freezeTimeFormat;
				}

				public boolean isFreezePreventsJoin() {
					return freezePreventsJoin;
				}

				public boolean isFreezePreventsLeave() {
					return freezePreventsLeave;
				}

				public boolean isFreezePreventsDisband() {
					return freezePreventsDisband;
				}

				public double getFreezeKickPenalty() {
					return freezeKickPenalty;
				}

				public double getMinDTR() {
					return minDTR;
				}

				public double getPerPlayer() {
					return perPlayer;
				}

				public double getRegainPerMinutePerPlayer() {
					return regainPerMinutePerPlayer;
				}

				public double getRegainPerMinuteMaxRate() {
					return regainPerMinuteMaxRate;
				}

				public double getMaxDTR() {
					return maxDTR;
				}

				public double getStartingDTR() {
					return startingDTR;
				}

				/**
				 * Not used directly by the plugin, as it uses the helper method.
				 *
				 * @return loss per death
				 * @see #getLossPerDeath(World)
				 */
				public double getLossPerDeathBase() {
					return this.lossPerDeath;
				}

				public double getLossPerDeath(World world) {
					if(this.worldDeathModifiers == null) {
						this.worldDeathModifiers = new HashMap<>();
					}
					return this.lossPerDeath * this.worldDeathModifiers.getOrDefault(world.getName(), 1D).doubleValue();
				}
			}

			public static class Power {
				private final double playerMin = -10.0D;
				private final double playerMax = 10.0D;
				private final double playerStarting = 0.0D;
				private final double powerPerMinute = 0.2;
				private final double lossPerDeath = 4.0;
				private final boolean regenOffline = false;
				private final double offlineLossPerDay = 0.0;
				private final double offlineLossLimit = 0.0;
				private final double factionMax = 0.0;
				private final boolean respawnHomeFromNoPowerLossWorlds = true;
				private final Set<String> worldsNoPowerLoss = new HashSet<String>() {
					{
						this.add("exampleWorld");
					}
				};
				private final boolean peacefulMembersDisablePowerLoss = true;
				private final boolean warZonePowerLoss = true;
				private final boolean wildernessPowerLoss = true;
				private final boolean canLeaveWithNegativePower = true;
				private final boolean raidability = false;
				private final boolean raidabilityOnEqualLandAndPower = true;
				private final int powerFreeze = 0;
				private final double vampirism = 0;

				public boolean isRaidability() {
					return raidability;
				}

				public boolean isRaidabilityOnEqualLandAndPower() {
					return raidabilityOnEqualLandAndPower;
				}

				public int getPowerFreeze() {
					return powerFreeze;
				}

				public boolean canLeaveWithNegativePower() {
					return canLeaveWithNegativePower;
				}

				public boolean isWarZonePowerLoss() {
					return warZonePowerLoss;
				}

				public boolean isWildernessPowerLoss() {
					return wildernessPowerLoss;
				}

				public double getPlayerMin() {
					return playerMin;
				}

				public double getPlayerMax() {
					return playerMax;
				}

				public double getPlayerStarting() {
					return playerStarting;
				}

				public double getPowerPerMinute() {
					return powerPerMinute;
				}

				public double getLossPerDeath() {
					return lossPerDeath;
				}

				public boolean isRegenOffline() {
					return regenOffline;
				}

				public double getOfflineLossPerDay() {
					return offlineLossPerDay;
				}

				public double getOfflineLossLimit() {
					return offlineLossLimit;
				}

				public double getFactionMax() {
					return factionMax;
				}

				public boolean isRespawnHomeFromNoPowerLossWorlds() {
					return respawnHomeFromNoPowerLossWorlds;
				}

				public Set<String> getWorldsNoPowerLoss() {
					return Collections.unmodifiableSet(worldsNoPowerLoss);
				}

				public boolean isPeacefulMembersDisablePowerLoss() {
					return peacefulMembersDisablePowerLoss;
				}

				public double getVampirism() {
					return vampirism;
				}
			}

			private final String system = "power";
			private final DTR dtr = new DTR();
			private final Power power = new Power();

			public String getSystem() {
				return system;
			}

			public DTR dtr() {
				return this.dtr;
			}

			public Power power() {
				return power;
			}
		}

		public static class Prefix {
			private final String admin = "***";
			private final String coleader = "**";
			private final String mod = "*";
			private final String normal = "+";
			private final String recruit = "-";

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

		public static class Chat {
			private final boolean factionOnlyChat = true;
			// Configuration on the Faction tag in chat messages.
			private final boolean tagHandledByAnotherPlugin = false;
			private final boolean tagRelationColored = true;
			private final String tagReplaceString = "[FACTION]";
			private final String tagInsertAfterString = "";
			private final String tagInsertBeforeString = "";
			private final int tagInsertIndex = 0;
			private final boolean tagPadBefore = false;
			private final boolean tagPadAfter = true;
			private final String tagFormat = "%s\u00A7f";
			private final boolean alwaysShowChatTag = true;
			private final String factionChatFormat = "%s:\u00A7f %s";
			private final String allianceChatFormat = "\u00A7d%s:\u00A7f %s";
			private final String truceChatFormat = "\u00A75%s:\u00A7f %s";
			private final String modChatFormat = "\u00A7c%s:\u00A7f %s";
			private final boolean broadcastDescriptionChanges = false;
			private final boolean broadcastTagChanges = false;

			public boolean isFactionOnlyChat() {
				return factionOnlyChat;
			}

			public boolean isTagHandledByAnotherPlugin() {
				return tagHandledByAnotherPlugin;
			}

			public boolean isTagRelationColored() {
				return tagRelationColored;
			}

			public String getTagReplaceString() {
				return tagReplaceString;
			}

			public String getTagInsertAfterString() {
				return tagInsertAfterString;
			}

			public String getTagInsertBeforeString() {
				return tagInsertBeforeString;
			}

			public int getTagInsertIndex() {
				return tagInsertIndex;
			}

			public boolean isTagPadBefore() {
				return tagPadBefore;
			}

			public boolean isTagPadAfter() {
				return tagPadAfter;
			}

			public String getTagFormat() {
				return tagFormat;
			}

			public boolean isAlwaysShowChatTag() {
				return alwaysShowChatTag;
			}

			public String getFactionChatFormat() {
				return factionChatFormat;
			}

			public String getAllianceChatFormat() {
				return allianceChatFormat;
			}

			public String getTruceChatFormat() {
				return truceChatFormat;
			}

			public String getModChatFormat() {
				return modChatFormat;
			}

			public boolean isBroadcastDescriptionChanges() {
				return broadcastDescriptionChanges;
			}

			public boolean isBroadcastTagChanges() {
				return broadcastTagChanges;
			}
		}

		public static class Homes {
			private final boolean enabled = true;
			private final boolean mustBeInClaimedTerritory = true;
			private final boolean teleportToOnDeath = true;
			private final boolean teleportCommandEnabled = true;
			private final boolean teleportAllowedFromEnemyTerritory = true;
			private final boolean teleportAllowedFromDifferentWorld = true;
			private final double teleportAllowedEnemyDistance = 32.0;
			private final boolean teleportIgnoreEnemiesIfInOwnTerritory = true;

			public boolean isEnabled() {
				return enabled;
			}

			public boolean isMustBeInClaimedTerritory() {
				return mustBeInClaimedTerritory;
			}

			public boolean isTeleportToOnDeath() {
				return teleportToOnDeath;
			}

			public boolean isTeleportCommandEnabled() {
				return teleportCommandEnabled;
			}

			public boolean isTeleportAllowedFromEnemyTerritory() {
				return teleportAllowedFromEnemyTerritory;
			}

			public boolean isTeleportAllowedFromDifferentWorld() {
				return teleportAllowedFromDifferentWorld;
			}

			public double getTeleportAllowedEnemyDistance() {
				return teleportAllowedEnemyDistance;
			}

			public boolean isTeleportIgnoreEnemiesIfInOwnTerritory() {
				return teleportIgnoreEnemiesIfInOwnTerritory;
			}
		}

		public static class PVP {
			private final boolean disablePVPBetweenNeutralFactions = false;
			private final boolean disablePVPForFactionlessPlayers = false;
			private final boolean enablePVPAgainstFactionlessInAttackersLand = false;
			private final boolean disablePeacefulPVPInWarzone = true;
			private final int noPVPDamageToOthersForXSecondsAfterLogin = 3;
			private final Set<String> worldsIgnorePvP = new HashSet<String>() {
				{
					this.add("exampleWorldName");
				}
			};

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
			private final boolean peacefulTerritoryDisablePVP = true;
			private final boolean peacefulTerritoryDisableMonsters = false;
			private final boolean peacefulTerritoryDisableBoom = false;
			private final boolean permanentFactionsDisableLeaderPromotion = false;
			private Set<String> ignoreBuildMaterials = new HashSet<>();

			{
				ignoreBuildMaterials.add("AIR");
			}

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

		public static class Portals {
			private final boolean limit = false;
			private final String minimumRelation = "MEMBER";

			public boolean isLimit() {
				return limit;
			}

			public String getMinimumRelation() {
				return minimumRelation;
			}
		}

		public static class Claims {
			private final boolean mustBeConnected = false;
			private final boolean canBeUnconnectedIfOwnedByOtherFaction = true;
			private final int requireMinFactionMembers = 1;
			private final int landsMax = 0;
			private final int lineClaimLimit = 5;
			private final int fillClaimMaxClaims = 25;
			private final int fillClaimMaxDistance = 5;
			private final int radiusClaimFailureLimit = 9;
			private final Set<String> worldsNoClaiming = new HashSet<String>() {
				{
					this.add("exampleWorldName");
				}
			};
			private final int bufferZone = 0;
			private final boolean allowOverClaim = true;
			private final boolean allowOverClaimIgnoringBuffer = false;

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

			public int getLandsMax() {
				return landsMax;
			}

			public int getFillClaimMaxClaims() {
				return fillClaimMaxClaims;
			}

			public int getFillClaimMaxDistance() {
				return fillClaimMaxDistance;
			}

			public int getLineClaimLimit() {
				return lineClaimLimit;
			}

			public int getRadiusClaimFailureLimit() {
				return radiusClaimFailureLimit;
			}

			public Set<String> getWorldsNoClaiming() {
				return Collections.unmodifiableSet(worldsNoClaiming);
			}
		}

		public static class Protection {
			private final Set<String> permanentFactionMemberDenyCommands = new HashSet<String>() {
				{
					this.add("exampleCommand");
				}
			};
			private final Set<String> territoryNeutralDenyCommands = new HashSet<String>() {
				{
					this.add("exampleCommand");
				}
			};
			private final Set<String> territoryEnemyDenyCommands = new HashSet<String>() {
				{
					this.add("home");
					this.add("sethome");
					this.add("spawn");
					this.add("tpahere");
					this.add("tpaccept");
					this.add("tpa");
				}
			};
			private final Set<String> territoryAllyDenyCommands = new HashSet<String>() {
				{
					this.add("exampleCommand");
				}
			};
			private final Set<String> warzoneDenyCommands = new HashSet<String>() {
				{
					this.add("exampleCommand");
				}
			};
			private final Set<String> wildernessDenyCommands = new HashSet<String>() {
				{
					this.add("exampleCommand");
				}
			};

			private final boolean territoryBlockCreepers = false;
			private final boolean territoryBlockCreepersWhenOffline = false;
			private final boolean territoryBlockFireballs = false;
			private final boolean territoryBlockFireballsWhenOffline = false;
			private final boolean territoryBlockTNT = false;
			private final boolean territoryBlockTNTWhenOffline = false;
			private final boolean territoryBlockOtherExplosions = false;
			private final boolean territoryBlockOtherExplosionsWhenOffline = false;
			private final boolean territoryDenyEndermanBlocks = true;
			private final boolean territoryDenyEndermanBlocksWhenOffline = true;
			private final boolean territoryBlockEntityDamageMatchingPerms = false;

			private final boolean safeZoneDenyBuild = true;
			private final boolean safeZoneDenyUsage = true;
			private final boolean safeZoneBlockTNT = true;
			private final boolean safeZoneBlockOtherExplosions = true;
			private final boolean safeZonePreventAllDamageToPlayers = false;
			private final boolean safeZoneDenyEndermanBlocks = true;
			private final boolean safeZoneBlockAllEntityDamage = false;

			private final boolean peacefulBlockAllEntityDamage = false;

			private final boolean warZoneDenyBuild = true;
			private final boolean warZoneDenyUsage = true;
			private final boolean warZoneBlockCreepers = true;
			private final boolean warZoneBlockFireballs = true;
			private final boolean warZoneBlockTNT = true;
			private final boolean warZoneBlockOtherExplosions = true;
			private final boolean warZoneFriendlyFire = false;
			private final boolean warZoneDenyEndermanBlocks = true;

			private final boolean wildernessDenyBuild = false;
			private final boolean wildernessDenyUsage = false;
			private final boolean wildernessBlockCreepers = false;
			private final boolean wildernessBlockFireballs = false;
			private final boolean wildernessBlockTNT = false;
			private final boolean wildernessBlockOtherExplosions = false;
			private final boolean wildernessDenyEndermanBlocks = false;

			private final boolean pistonProtectionThroughDenyBuild = true;

			private final Set<String> territoryDenyUsageMaterials = new HashSet<>();
			private final Set<String> territoryDenyUsageMaterialsWhenOffline = new HashSet<>();
			private transient Set<Material> territoryDenyUsageMaterialsMat;
			private transient Set<Material> territoryDenyUsageMaterialsWhenOfflineMat;
			private final Set<String> containerExceptions = new HashSet<>();
			private transient Set<Material> containerExceptionsMat;
			private final Set<String> breakExceptions = new HashSet<>();
			private transient Set<Material> breakExceptionsMat;
			private final Set<String> entityInteractExceptions = new HashSet<>();
			private final Set<String> playersWhoBypassAllProtection = new HashSet<String>() {
				{
					this.add("example-player-name");
				}
			};
			private final Set<String> worldsNoWildernessProtection = new HashSet<String>() {
				{
					this.add("exampleWorld");
				}
			};

			private Protection() {
				protectUsage("FIRE_CHARGE");
				protectUsage("FLINT_AND_STEEL");
				protectUsage("BUCKET");
				protectUsage("WATER_BUCKET");
				protectUsage("LAVA_BUCKET");
			}

			private void protectUsage(String material) {
				territoryDenyUsageMaterials.add(material);
				territoryDenyUsageMaterialsWhenOffline.add(material);
			}

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
			private final Set<String> preventSpawningInSafezone = new HashSet<String>() {
				{
					this.add("BREEDING");
					this.add("BUILD_IRONGOLEM");
					this.add("BUILD_SNOWMAN");
					this.add("BUILD_WITHER");
					this.add("CURED");
					this.add("DEFAULT");
					this.add("DISPENSE_EGG");
					this.add("DROWNED");
					this.add("EGG");
					this.add("ENDER_PEARL");
					this.add("EXPLOSION");
					this.add("INFECTION");
					this.add("LIGHTNING");
					this.add("MOUNT");
					this.add("NATURAL");
					this.add("NETHER_PORTAL");
					this.add("OCELOT_BABY");
					this.add("PATROL");
					this.add("RAID");
					this.add("REINFORCEMENTS");
					this.add("SILVERFISH_BLOCK");
					this.add("SLIME_SPLIT");
					this.add("SPAWNER");
					this.add("SPAWNER_EGG");
					this.add("TRAP");
					this.add("VILLAGE_DEFENSE");
					this.add("VILLAGE_INVASION");
				}
			};
			private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInSafezoneReason;
			private final Set<String> preventSpawningInSafezoneExceptions = new HashSet<String>() {
				{
					this.add("BAT");
					this.add("CAT");
					this.add("CHICKEN");
					this.add("COD");
					this.add("COW");
					this.add("DOLPHIN");
					this.add("DONKEY");
					this.add("FOX");
					this.add("HORSE");
					this.add("IRON_GOLEM");
					this.add("LLAMA");
					this.add("MULE");
					this.add("MUSHROOM_COW");
					this.add("OCELOT");
					this.add("PANDA");
					this.add("PARROT");
					this.add("PIG");
					this.add("POLAR_BEAR");
					this.add("PUFFERFISH");
					this.add("RABBIT");
					this.add("SALMON");
					this.add("SHEEP");
					this.add("STRIDER");
					this.add("SQUID");
					this.add("TRADER_LLAMA");
					this.add("TROPICAL_FISH");
					this.add("TURTLE");
					this.add("VILLAGER");
					this.add("WANDERING_TRADER");
					this.add("WOLF");
				}
			};
			private transient Set<EntityType> preventSpawningInSafezoneExceptionsType;
			private final Set<String> preventSpawningInWarzone = new HashSet<>();
			private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInWarzoneReason;
			private final Set<String> preventSpawningInWarzoneExceptions = new HashSet<>();
			private transient Set<EntityType> preventSpawningInWarzoneExceptionsType;
			private final Set<String> preventSpawningInWilderness = new HashSet<>();
			private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInWildernessReason;
			private final Set<String> preventSpawningInWildernessExceptions = new HashSet<>();
			private transient Set<EntityType> preventSpawningInWildernessExceptionsType;
			private final Set<String> preventSpawningInTerritory = new HashSet<>();
			private transient Set<CreatureSpawnEvent.SpawnReason> preventSpawningInTerritoryReason;
			private final Set<String> preventSpawningInTerritoryExceptions = new HashSet<>();
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
			private final boolean enabled = true;
			private final int limitPerFaction = 0;
			private final boolean moderatorsBypass = true;
			private final boolean denyBuild = true;
			private final boolean painBuild = false;
			private final boolean protectMaterials = true;
			private final boolean denyUsage = true;

			private final boolean messageOnBorder = true;
			private final boolean messageInsideTerritory = true;
			private final boolean messageByChunk = false;

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
			private final boolean allowMultipleColeaders = false;
			private final int tagLengthMin = 3;
			private final int tagLengthMax = 10;
			private final boolean tagForceUpperCase = false;

			private final boolean newFactionsDefaultOpen = false;
			private final boolean newFactionsDefaultPeaceful = false;
			private final int factionMemberLimit = 0;
			private final String newPlayerStartingFactionID = "0";

			private final double saveToFileEveryXMinutes = 30.0;

			private final double autoLeaveAfterDaysOfInactivity = 10.0;
			private final double autoLeaveRoutineRunsEveryXMinutes = 5.0;
			private final int autoLeaveRoutineMaxMillisecondsPerTick = 5;  // 1 server tick is roughly 50ms, so default max 10% of a tick
			private final boolean removePlayerDataWhenBanned = true;
			private final boolean autoLeaveDeleteFPlayerData = true; // Let them just remove player from Faction.
			private final double considerFactionsReallyOfflineAfterXMinutes = 0.0;
			private final int actionDeniedPainAmount = 1;
			private final String defaultRelation = "neutral";
			private final String defaultRole = "member";
			private transient Role defaultRoleRole;
			private final boolean disablePistonsInTerritory = false;
			private final List<String> nameBlacklist = new ArrayList<String>() {
				{
					this.add("blockedwordhere");
					this.add("anotherblockedthinghere");
				}
			};

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

		private final Chat chat = new Chat();
		private final Homes homes = new Homes();
		private final PVP pvp = new PVP();
		private final SpecialCase specialCase = new SpecialCase();
		private final Claims claims = new Claims();
		private final Portals portals = new Portals();
		private final Protection protection = new Protection();
		private final OwnedArea ownedArea = new OwnedArea();
		private final Prefix prefixes = new Prefix();
		private final LandRaidControl landRaidControl = new LandRaidControl();
		private final Other other = new Other();
		private final Spawning spawning = new Spawning();

		public Chat chat() {
			return chat;
		}

		public Homes homes() {
			return homes;
		}

		public PVP pvp() {
			return pvp;
		}

		public SpecialCase specialCase() {
			return specialCase;
		}

		public Claims claims() {
			return claims;
		}

		public Portals portals() {
			return portals;
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

		public Prefix prefixes() {
			return prefixes;
		}

		public LandRaidControl landRaidControl() {
			return landRaidControl;
		}

		public Spawning spawning() {
			return spawning;
		}
	}

	public static class Logging {
		private final boolean factionCreate = true;
		private final boolean factionDisband = true;
		private final boolean factionJoin = true;
		private final boolean factionKick = true;
		private final boolean factionLeave = true;
		private final boolean landClaims = true;
		private final boolean landUnclaims = true;
		private final boolean playerCommands = true;

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
		private final int width = 49;
		private final boolean showFactionKey = true;
		private final boolean showNeutralFactionsOnMap = true;
		private final boolean showEnemyFactions = true;
		private final boolean showTruceFactions = true;

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
		private final boolean restrictWorlds = false;
		private final boolean whitelist = true;
		private final Set<String> worldList = new HashSet<String>() {
			{
				this.add("exampleWorld");
			}
		};

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
			private final boolean enabled = false;
			private final String title = "Faction Status";
			private final boolean prefixes = true;
			private final String prefixTemplate = "{relationcolor}[{faction}] &r";
			private final boolean suffixes = false;
			private final String suffixTemplate = " {relationcolor}[{faction}]";

			private final List<String> content = new ArrayList<String>() {
				{
					this.add("&6Your Faction");
					this.add("{faction}");
					this.add("&3Your Power");
					this.add("{power}");
				}
			};
			private final boolean factionlessEnabled = false;
			private final List<String> factionlessContent = new ArrayList<String>() {
				{
					this.add("Make a new Faction");
					this.add("Use /f create");
				}
			};
			private final String factionlessTitle = "Status";

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
			private final int expiration = 7;
			private final boolean enabled = false;
			private final List<String> content = new ArrayList<String>() {
				{
					this.add("&6Power");
					this.add("{power}");
					this.add("&3Members");
					this.add("{online}/{members}");
					this.add("&4Leader");
					this.add("{leader}");
					this.add("&bTerritory");
					this.add("{chunks}");
				}
			};
			private final String title = "{faction-relation-color}{faction}";

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

		private final Constant constant = new Constant();
		private final Info info = new Info();

		public Constant constant() {
			return constant;
		}

		public Info info() {
			return info;
		}
	}

	public static class LWC {
		private final boolean enabled = true;
		private final boolean resetLocksOnUnclaim = false;
		private final boolean resetLocksOnCapture = false;

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
		private final boolean checking = false;
		private final boolean buildPriority = false;

		public boolean isChecking() {
			return checking;
		}

		public boolean isBuildPriority() {
			return buildPriority;
		}
	}

	private List<String> commandBase = new ArrayList<String>() {
		{
			this.add("f");
		}
	};
	private final Colors colors = new Colors();
	private final Commands commands = new Commands();
	private final Factions factions = new Factions();
	private final Logging logging = new Logging();
	private final MapSettings map = new MapSettings();
	private final RestrictWorlds restrictWorlds = new RestrictWorlds();
	private final Scoreboard scoreboard = new Scoreboard();
	private final LWC lwc = new LWC();
	private final WorldGuard worldGuard = new WorldGuard();

	public List<String> getCommandBase() {
		return commandBase == null ? (commandBase = Collections.singletonList("f")) : commandBase;
	}

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
