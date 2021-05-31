package com.massivecraft.factions.configuration;

import com.massivecraft.factions.perms.Permissible;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import org.bukkit.plugin.Plugin;

public class DefaultPermissionsConfiguration extends AbstractConfiguration {

	public DefaultPermissionsConfiguration(Plugin plugin) {
		super(plugin, "default_permissions.yml");
	}

	public static class Permissions {
		public static class PermissiblePermInfo {
			public boolean isLocked() {
				return this.locked;
			}

			public boolean defaultAllowed() {
				return this.value;
			}

			private final boolean locked = false;

			private boolean value = false;
		}

		public static class FactionOnlyPermInfo {
			protected final PermissiblePermInfo coleader = new PermissiblePermInfo();
			protected final PermissiblePermInfo moderator = new PermissiblePermInfo();
			protected final PermissiblePermInfo normal = new PermissiblePermInfo();
			protected final PermissiblePermInfo recruit = new PermissiblePermInfo();

			public PermissiblePermInfo get(Permissible permissible) {
				if(permissible instanceof Role) {
					switch((Role) permissible) {
						case COLEADER:
							return this.coleader;
						case MODERATOR:
							return this.moderator;
						case NORMAL:
							return this.normal;
						case RECRUIT:
							return this.recruit;
					}
				}
				// TODO print warning
				return null;
			}
		}

		public static class FullPermInfo extends FactionOnlyPermInfo {
			protected final PermissiblePermInfo ally = new PermissiblePermInfo();
			protected final PermissiblePermInfo truce = new PermissiblePermInfo();
			protected final PermissiblePermInfo neutral = new PermissiblePermInfo();
			protected final PermissiblePermInfo enemy = new PermissiblePermInfo();

			public PermissiblePermInfo get(Permissible permissible) {
				if(permissible instanceof Relation) {
					switch((Relation) permissible) {
						case ALLY:
							return this.ally;
						case TRUCE:
							return this.truce;
						case NEUTRAL:
							return this.neutral;
						case ENEMY:
							return this.enemy;
					}
				}
				return super.get(permissible);
			}
		}

		public FactionOnlyPermInfo getBan() {
			return this.ban;
		}

		public FullPermInfo getBuild() {
			return this.build;
		}

		public FullPermInfo getDestroy() {
			return this.destroy;
		}

		public FullPermInfo getFrostWalk() {
			return this.frostWalk;
		}

		public FullPermInfo getPainBuild() {
			return this.painBuild;
		}

		public FullPermInfo getDoor() {
			return this.door;
		}

		public FullPermInfo getButton() {
			return this.button;
		}

		public FullPermInfo getLever() {
			return this.lever;
		}

		public FullPermInfo getContainer() {
			return this.container;
		}

		public FactionOnlyPermInfo getInvite() {
			return this.invite;
		}

		public FactionOnlyPermInfo getKick() {
			return this.kick;
		}

		public FullPermInfo getItem() {
			return this.item;
		}

		public FullPermInfo getHome() {
			return this.home;
		}

		public FactionOnlyPermInfo getSetHome() {
			return this.sethome;
		}

		public FactionOnlyPermInfo getListClaims() {
			return this.listClaims;
		}

		public FactionOnlyPermInfo getTerritory() {
			return this.territory;
		}

		public FactionOnlyPermInfo getOwner() {
			return this.owner;
		}

		public FullPermInfo getPlate() {
			return this.plate;
		}

		public FactionOnlyPermInfo getDisband() {
			return this.disband;
		}

		public FactionOnlyPermInfo getPromote() {
			return this.promote;
		}

		public FactionOnlyPermInfo getSetWarp() {
			return this.setwarp;
		}

		public FullPermInfo getWarp() {
			return this.warp;
		}

		private final FactionOnlyPermInfo ban = new FactionOnlyPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
			}
		};

		private final FullPermInfo build = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
			}
		};

		private final FullPermInfo destroy = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
			}
		};

		private final FullPermInfo frostWalk = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
			}
		};

		private final FullPermInfo painBuild = new FullPermInfo();

		private final FullPermInfo door = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
				this.ally.value = true;
			}
		};

		private final FullPermInfo button = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
				this.ally.value = true;
			}
		};

		private final FullPermInfo lever = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
				this.ally.value = true;
			}
		};

		private final FullPermInfo container = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
			}
		};

		private final FactionOnlyPermInfo invite = new FactionOnlyPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
			}
		};
		private final FactionOnlyPermInfo kick = new FactionOnlyPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
			}
		};

		private final FullPermInfo item = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
			}
		};

		private final FullPermInfo home = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
			}
		};

		private final FactionOnlyPermInfo listClaims = new FactionOnlyPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
			}
		};

		private final FactionOnlyPermInfo sethome = new FactionOnlyPermInfo() {
			{
				this.coleader.value = true;
			}
		};

		private final FactionOnlyPermInfo territory = new FactionOnlyPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
			}
		};

		private final FactionOnlyPermInfo owner = new FactionOnlyPermInfo();

		private final FullPermInfo plate = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
				this.ally.value = true;
			}
		};
		private final FactionOnlyPermInfo disband = new FactionOnlyPermInfo();

		private final FactionOnlyPermInfo promote = new FactionOnlyPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
			}
		};

		private final FactionOnlyPermInfo setwarp = new FactionOnlyPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
			}
		};

		private final FullPermInfo warp = new FullPermInfo() {
			{
				this.coleader.value = true;
				this.moderator.value = true;
				this.normal.value = true;
				this.recruit.value = true;
			}
		};

	}

	private final Permissions permissions = new Permissions();

	public Permissions getPermissions() {
		return this.permissions;
	}
}