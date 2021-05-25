package com.massivecraft.factions.util;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IRelationParticipator;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.ChatColor;

public abstract class RelationUtil {

	public static String describeThatToMe(IRelationParticipator that, IRelationParticipator me, boolean ucfirst) {
		String ret = "";

		IFaction thatFaction = getFaction(that);
		if(thatFaction == null) {
			return "ERROR"; // ERROR
		}

		IFaction myFaction = getFaction(me);
//		if (myFaction == null) return that.describeTo(null); // no relation, but can show basic name or tag

		if(that instanceof IFaction) {
			if(me instanceof IFactionPlayer && myFaction == thatFaction) {
				ret = Localization.GENERIC_YOURFACTION.toString();
			} else {
				ret = thatFaction.getTag();
			}
		} else if(that instanceof IFactionPlayer) {
			IFactionPlayer fplayerthat = (IFactionPlayer) that;
			if(that == me) {
				ret = Localization.GENERIC_YOU.toString();
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

	public static String describeThatToMe(IRelationParticipator that, IRelationParticipator me) {
		return describeThatToMe(that, me, false);
	}

	public static Relation getRelationTo(IRelationParticipator me, IRelationParticipator that) {
		return getRelationTo(that, me, false);
	}

	public static Relation getRelationTo(IRelationParticipator me, IRelationParticipator that, boolean ignorePeaceful) {
		IFaction fthat = getFaction(that);
		if(fthat == null) {
			return Relation.NEUTRAL; // ERROR
		}

		IFaction fme = getFaction(me);
		if(fme == null) {
			return Relation.NEUTRAL; // ERROR
		}

		if(!fthat.isNormal() || !fme.isNormal()) {
			return Relation.NEUTRAL;
		}

		if(fthat.equals(fme)) {
			return Relation.MEMBER;
		}

		if(!ignorePeaceful && (fme.isPeaceful() || fthat.isPeaceful())) {
			return Relation.NEUTRAL;
		}

		if(fme.getRelationWish(fthat).value >= fthat.getRelationWish(fme).value) {
			return fthat.getRelationWish(fme);
		}

		return fme.getRelationWish(fthat);
	}

	public static IFaction getFaction(IRelationParticipator rp) {
		if(rp instanceof IFaction) {
			return (IFaction) rp;
		}

		if(rp instanceof IFactionPlayer) {
			return ((IFactionPlayer) rp).getFaction();
		}

		// ERROR
		return null;
	}

	public static ChatColor getColorOfThatToMe(IRelationParticipator that, IRelationParticipator me) {
		IFaction thatFaction = getFaction(that);
		if(thatFaction != null) {
			if(thatFaction.isPeaceful() && thatFaction != getFaction(me)) {
				return FactionsPlugin.getInstance().conf().colors().relations().getPeaceful();
			}

			if(thatFaction.isSafeZone() && thatFaction != getFaction(me)) {
				return FactionsPlugin.getInstance().conf().colors().factions().getSafezone();
			}

			if(thatFaction.isWarZone() && thatFaction != getFaction(me)) {
				return FactionsPlugin.getInstance().conf().colors().factions().getWarzone();
			}
		}

		return getRelationTo(that, me).getColor();
	}
}
