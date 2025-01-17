package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.RelationParticipator;
import com.massivecraft.factions.combat.Setting;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.ChatColor;

public class RelationUtil {

	public static String describeThatToMe(RelationParticipator that, RelationParticipator me, boolean ucfirst) {
		String ret = "";

		Faction thatFaction = getFaction(that);
		if(thatFaction == null) {
			return "ERROR"; // ERROR
		}

		Faction myFaction = getFaction(me);
//		if (myFaction == null) return that.describeTo(null); // no relation, but can show basic name or tag

		if(that instanceof Faction) {
			if(me instanceof FPlayer && myFaction == thatFaction) {
				ret = TL.GENERIC_YOURFACTION.toString();
			} else {
				ret = thatFaction.getTag();
			}
		} else if(that instanceof FPlayer) {
			FPlayer fplayerthat = (FPlayer) that;
			if(that == me) {
				ret = TL.GENERIC_YOU.toString();
			} else if(thatFaction == myFaction) {
				ret = fplayerthat.getNameAndTitle();
			} else {
				ret = fplayerthat.getNameAndTag();
			}
		}

		if(ucfirst) {
			ret = TextUtil.upperCaseFirst(ret);
		}

		return "" + getColorOfThatToMe(that, me) + ret;
	}

	public static String describeThatToMe(RelationParticipator that, RelationParticipator me) {
		return describeThatToMe(that, me, false);
	}

	public static Relation getRelationTo(RelationParticipator me, RelationParticipator that) {
		return getRelationTo(that, me, false);
	}

	public static Relation getRelationTo(RelationParticipator me, RelationParticipator that, boolean ignorePeaceful) {
		Faction fthat = getFaction(that);
		if(fthat == null) {
			return Relation.NEUTRAL; // ERROR
		}

		Faction fme = getFaction(me);
		if(fme == null) {
			return Relation.NEUTRAL; // ERROR
		}

		if(!fthat.isNormal() || !fme.isNormal()) {
			return Relation.NEUTRAL;
		}

		if(fthat.equals(fme)) {
			return Relation.MEMBER;
		}

		if(!ignorePeaceful && (fme.getCombatSetting() == Setting.PREVENT_ALL | fthat.getCombatSetting() == Setting.PREVENT_ALL)) {
			return Relation.NEUTRAL;
		}

		if(fme.getRelationWish(fthat).value >= fthat.getRelationWish(fme).value) {
			return fthat.getRelationWish(fme);
		}

		return fme.getRelationWish(fthat);
	}

	public static Faction getFaction(RelationParticipator rp) {
		if(rp instanceof Faction) {
			return (Faction) rp;
		}

		if(rp instanceof FPlayer) {
			return ((FPlayer) rp).getFaction();
		}

		// ERROR
		return null;
	}

	public static ChatColor getColorOfThatToMe(RelationParticipator that, RelationParticipator me) {
		Faction thatFaction = getFaction(that);
		if(thatFaction != null) {
			if(thatFaction.getCombatSetting() == Setting.PREVENT_ALL && thatFaction != getFaction(me)) {
				return FactionsPlugin.getInstance().configMain.colors().relations().peaceful();
			}
		}

		return getRelationTo(that, me).getColor();
	}
}
