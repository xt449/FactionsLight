package com.massivecraft.factions;

import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;

public interface RelationParticipator {

	String describeTo(RelationParticipator that);

	String describeTo(RelationParticipator that, boolean ucfirst);

	Relation getRelationTo(RelationParticipator that);

	Relation getRelationTo(RelationParticipator that, boolean ignorePeaceful);

	ChatColor getColorTo(RelationParticipator to);

	void msg(String str, Object... args);

	void msg(TL translation, Object... args);
}
