package com.massivecraft.factions.perms;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;

public enum Role implements Permissible {
	ADMIN(4, TL.ROLE_ADMIN),
	COLEADER(3, TL.ROLE_COLEADER),
	MODERATOR(2, TL.ROLE_MODERATOR),
	NORMAL(1, TL.ROLE_NORMAL),
	RECRUIT(0, TL.ROLE_RECRUIT);

	public final int value;
	public final String nicename;
	public final TL translation;

	Role(final int value, final TL translation) {
		this.value = value;
		this.nicename = translation.toString();
		this.translation = translation;
	}

	public boolean isAtLeast(Role role) {
		return this.value >= role.value;
	}

	public static Role getRelative(Role role, int relative) {
		return Role.getByValue(role.value + relative);
	}

	public static Role getByValue(int value) {
		switch(value) {
			case 0:
				return RECRUIT;
			case 1:
				return NORMAL;
			case 2:
				return MODERATOR;
			case 3:
				return COLEADER;
			case 4:
				return ADMIN;
		}

		return null;
	}

	public static Role fromString(String check) {
		switch(check.toLowerCase()) {
			case "admin":
				return ADMIN;
			case "coleader":
			case "coowner":
				return COLEADER;
			case "mod":
			case "moderator":
				return MODERATOR;
			case "normal":
			case "member":
				return NORMAL;
			case "recruit":
			case "rec":
				return RECRUIT;
		}

		return null;
	}

	@Override
	public String toString() {
		return this.nicename;
	}

	public TL getTranslation() {
		return translation;
	}

	public String getPrefix() {
		if(this == Role.ADMIN) {
			return FactionsPlugin.getInstance().configMain.factions().prefixes().admin();
		}

		if(this == Role.COLEADER) {
			return FactionsPlugin.getInstance().configMain.factions().prefixes().coleader();
		}

		if(this == Role.MODERATOR) {
			return FactionsPlugin.getInstance().configMain.factions().prefixes().mod();
		}

		if(this == Role.NORMAL) {
			return FactionsPlugin.getInstance().configMain.factions().prefixes().normal();
		}

		if(this == Role.RECRUIT) {
			return FactionsPlugin.getInstance().configMain.factions().prefixes().recruit();
		}

		return "";
	}

	@Override
	public ChatColor getColor() {
		return Relation.MEMBER.getColor();
	}
}
