package com.massivecraft.factions;

import com.massivecraft.factions.perms.Relation;
import org.bukkit.ChatColor;

public interface IRelationParticipator {

	String describeTo(IRelationParticipator that);

	String describeTo(IRelationParticipator that, boolean ucfirst);

	Relation getRelationTo(IRelationParticipator that);

	Relation getRelationTo(IRelationParticipator that, boolean ignorePeaceful);

	ChatColor getColorTo(IRelationParticipator to);
}
