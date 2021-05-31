package com.massivecraft.factions.perms;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;

public enum Relation implements Permissible {
	MEMBER(4, TL.RELATION_MEMBER_SINGULAR.toString()),
	ALLY(3, TL.RELATION_ALLY_SINGULAR.toString()),
	TRUCE(2, TL.RELATION_TRUCE_SINGULAR.toString()),
	NEUTRAL(1, TL.RELATION_NEUTRAL_SINGULAR.toString()),
	ENEMY(0, TL.RELATION_ENEMY_SINGULAR.toString());

	public final int value;
	public final String nicename;

	Relation(final int value, final String nicename) {
		this.value = value;
		this.nicename = nicename;
	}

	public static Relation fromString(String s) {
		if(s == null) {
			return NEUTRAL;
		}
		if(s.equalsIgnoreCase(MEMBER.nicename)) {
			return MEMBER;
		} else if(s.equalsIgnoreCase(ALLY.nicename)) {
			return ALLY;
		} else if(s.equalsIgnoreCase(TRUCE.nicename)) {
			return TRUCE;
		} else if(s.equalsIgnoreCase(ENEMY.nicename)) {
			return ENEMY;
		} else {
			switch(s.toUpperCase()) {
				case "MEMBER":
					return MEMBER;
				case "ALLY":
					return ALLY;
				case "TRUCE":
					return TRUCE;
				case "ENEMY":
					return ENEMY;
				case "NEUTRAL":
				default: // If they somehow mess things up, go back to default behavior.
					return NEUTRAL;
			}
		}
	}

	@Override
	public String toString() {
		return this.nicename;
	}

	public String getTranslation() {
		try {
			return TL.valueOf("RELATION_" + name() + "_SINGULAR").toString();
		} catch(IllegalArgumentException e) {
			return toString();
		}
	}

	public String getPluralTranslation() {
		for(TL t : TL.values()) {
			if(t.name().equalsIgnoreCase("RELATION_" + name() + "_PLURAL")) {
				return t.toString();
			}
		}
		return toString();
	}

	public boolean isMember() {
		return this == MEMBER;
	}

	public boolean isAlly() {
		return this == ALLY;
	}

	public boolean isTruce() {
		return this == TRUCE;
	}

	public boolean isNeutral() {
		return this == NEUTRAL;
	}

	public boolean isEnemy() {
		return this == ENEMY;
	}

	public boolean isAtLeast(Relation relation) {
		return this.value >= relation.value;
	}

	public boolean isAtMost(Relation relation) {
		return this.value <= relation.value;
	}

	public ChatColor getColor() {
		switch(this) {
			case MEMBER:
				return FactionsPlugin.getInstance().configMain.colors().relations().member();
			case ALLY:
				return FactionsPlugin.getInstance().configMain.colors().relations().ally();
			case NEUTRAL:
				return FactionsPlugin.getInstance().configMain.colors().relations().neutral();
			case TRUCE:
				return FactionsPlugin.getInstance().configMain.colors().relations().truce();
			default:
				return FactionsPlugin.getInstance().configMain.colors().relations().enemy();
		}
	}
}
