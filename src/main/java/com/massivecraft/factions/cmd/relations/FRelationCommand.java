package com.massivecraft.factions.cmd.relations;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Permission;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.combat.Setting;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.event.FactionRelationWishEvent;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class FRelationCommand extends FCommand {

	public final Relation targetRelation;

	public FRelationCommand(Relation targetRelation, String alias) {
		super();
		this.targetRelation = targetRelation;
		this.aliases.add(alias);
		this.requiredArgs.add("faction tag");

		this.requirements = new CommandRequirements.Builder(Permission.RELATION)
				.memberOnly()
				.withRole(Role.MODERATOR)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		Faction them = context.argAsFaction(0);
		if(them == null) {
			return;
		}

		if(!them.isNormal()) {
			context.msg(TL.COMMAND_RELATIONS_ALLTHENOPE);
			return;
		}

		if(them == context.faction) {
			context.msg(TL.COMMAND_RELATIONS_MORENOPE);
			return;
		}

		if(context.faction.getRelationWish(them) == targetRelation) {
			context.msg(TL.COMMAND_RELATIONS_ALREADYINRELATIONSHIP, them.getTag());
			return;
		}

		Relation oldRelation = context.faction.getRelationTo(them, true);
		FactionRelationWishEvent wishEvent = new FactionRelationWishEvent(context.fPlayer, context.faction, them, oldRelation, targetRelation);
		Bukkit.getPluginManager().callEvent(wishEvent);
		if(wishEvent.isCancelled()) {
			return;
		}

		// try to set the new relation
		context.faction.setRelationWish(them, targetRelation);
		Relation currentRelation = context.faction.getRelationTo(them, true);
		ChatColor currentRelationColor = currentRelation.getColor();

		// if the relation change was successful
		if(targetRelation.value == currentRelation.value) {
			// trigger the faction relation event
			FactionRelationEvent relationEvent = new FactionRelationEvent(context.faction, them, oldRelation, currentRelation);
			Bukkit.getServer().getPluginManager().callEvent(relationEvent);

			them.msg(TL.COMMAND_RELATIONS_MUTUAL, currentRelationColor + targetRelation.getTranslation(), currentRelationColor + context.faction.getTag());
			context.faction.msg(TL.COMMAND_RELATIONS_MUTUAL, currentRelationColor + targetRelation.getTranslation(), currentRelationColor + them.getTag());
		} else {
			// inform the other faction of your request
			them.msg(TL.COMMAND_RELATIONS_PROPOSAL_1, currentRelationColor + context.faction.getTag(), targetRelation.getColor() + targetRelation.getTranslation());
			them.msg(TL.COMMAND_RELATIONS_PROPOSAL_2, "f", targetRelation, context.faction.getTag());
			context.faction.msg(TL.COMMAND_RELATIONS_PROPOSAL_SENT, currentRelationColor + them.getTag(), "" + targetRelation.getColor() + targetRelation);
		}

		// TODO
		if(!targetRelation.isNeutral() && them.getCombatSetting() == Setting.PREVENT_ALL) {
			them.msg(TL.COMMAND_RELATIONS_PEACEFUL);
			context.faction.msg(TL.COMMAND_RELATIONS_PEACEFULOTHER);
		}

		if(!targetRelation.isNeutral() && context.faction.getCombatSetting() == Setting.PREVENT_ALL) {
			them.msg(TL.COMMAND_RELATIONS_PEACEFULOTHER);
			context.faction.msg(TL.COMMAND_RELATIONS_PEACEFUL);
		}

		FTeamWrapper.updatePrefixes(context.faction);
		FTeamWrapper.updatePrefixes(them);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_RELATIONS_DESCRIPTION;
	}
}
