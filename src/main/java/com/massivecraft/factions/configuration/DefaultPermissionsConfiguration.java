package com.massivecraft.factions.configuration;

import com.massivecraft.factions.perms.Permissible;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import org.bukkit.plugin.Plugin;

/**
 * @author Jonathan Talcott (xt449 / BinaryBanana)
 */
public class DefaultPermissionsConfiguration extends AbstractConfiguration {

	public DefaultPermissionsConfiguration(Plugin plugin) {
		super(plugin, "default_permissions.yml");
	}

	public static class PermissiblePermInfo {
		private transient boolean locked;
		private transient boolean value;

		public boolean isLocked() {
			return locked;
		}

		public boolean defaultAllowed() {
			return this.value;
		}
	}

	public static class FactionOnlyPermInfo {
		protected transient final PermissiblePermInfo coleader = new PermissiblePermInfo();
		protected transient final PermissiblePermInfo moderator = new PermissiblePermInfo();
		protected transient final PermissiblePermInfo normal = new PermissiblePermInfo();
		protected transient final PermissiblePermInfo recruit = new PermissiblePermInfo();

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
			System.out.println("Error loading some permission trash: " + getClass().getSimpleName() + ": " + permissible.name());
			return new PermissiblePermInfo();
		}
	}

	public static class FullPermInfo extends FactionOnlyPermInfo {
		protected transient final PermissiblePermInfo ally = new PermissiblePermInfo();
		protected transient final PermissiblePermInfo truce = new PermissiblePermInfo();
		protected transient final PermissiblePermInfo neutral = new PermissiblePermInfo();
		protected transient final PermissiblePermInfo enemy = new PermissiblePermInfo();

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

	private transient final FactionOnlyPermInfo ban = new FactionOnlyPermInfo();

	private transient final FullPermInfo build = new FullPermInfo();
	private transient final FullPermInfo destroy = new FullPermInfo();
	private transient final FullPermInfo frostWalk = new FullPermInfo();
	private transient final FullPermInfo painBuild = new FullPermInfo();
	private transient final FullPermInfo door = new FullPermInfo();
	private transient final FullPermInfo button = new FullPermInfo();
	private transient final FullPermInfo lever = new FullPermInfo();
	private transient final FullPermInfo container = new FullPermInfo();

	private transient final FactionOnlyPermInfo invite = new FactionOnlyPermInfo();
	private transient final FactionOnlyPermInfo kick = new FactionOnlyPermInfo();

	private transient final FullPermInfo item = new FullPermInfo();

	private transient final FactionOnlyPermInfo listClaims = new FactionOnlyPermInfo();
	private transient final FactionOnlyPermInfo territory = new FactionOnlyPermInfo();
	private transient final FactionOnlyPermInfo owner = new FactionOnlyPermInfo();

	private transient final FullPermInfo plate = new FullPermInfo();

	private transient final FactionOnlyPermInfo disband = new FactionOnlyPermInfo();
	private transient final FactionOnlyPermInfo promote = new FactionOnlyPermInfo();

	public final FactionOnlyPermInfo getBan() {
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
}
