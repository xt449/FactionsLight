package com.massivecraft.factions.cmd.relations;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FactionRelationEvent;
import com.massivecraft.factions.event.FactionRelationWishEvent;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class FRelationCommand extends FCommand {

	public Relation targetRelation;

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
		IFaction them = context.argAsFaction(0);
		if(them == null) {
			return;
		}

		if(!them.isNormal()) {
			context.msg(Localization.COMMAND_RELATIONS_ALLTHENOPE);
			return;
		}

		if(them == context.faction) {
			context.msg(Localization.COMMAND_RELATIONS_MORENOPE);
			return;
		}

		if(context.faction.getRelationWish(them) == targetRelation) {
			context.msg(Localization.COMMAND_RELATIONS_ALREADYINRELATIONSHIP, them.getTag());
			return;
		}

		if(hasMaxRelations(them, targetRelation, context)) {
			// We message them down there with the count.
			return;
		}
		Relation oldRelation = context.faction.getRelationTo(them, true);
		FactionRelationWishEvent wishEvent = new FactionRelationWishEvent(context.fPlayer, context.faction, them, oldRelation, targetRelation);
		Bukkit.getPluginManager().callEvent(wishEvent);
		if(wishEvent.isCancelled()) {
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if(!context.payForCommand(targetRelation.getRelationCost(), Localization.COMMAND_RELATIONS_TOMARRY, Localization.COMMAND_RELATIONS_FORMARRY)) {
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

			them.msg(Localization.COMMAND_RELATIONS_MUTUAL, currentRelationColor + targetRelation.getTranslation(), currentRelationColor + context.faction.getTag());
			context.faction.msg(Localization.COMMAND_RELATIONS_MUTUAL, currentRelationColor + targetRelation.getTranslation(), currentRelationColor + them.getTag());
		} else {
			// inform the other faction of your request
			them.msg(Localization.COMMAND_RELATIONS_PROPOSAL_1, currentRelationColor + context.faction.getTag(), targetRelation.getColor() + targetRelation.getTranslation());
			them.msg(Localization.COMMAND_RELATIONS_PROPOSAL_2, FactionsPlugin.getInstance().conf().getCommandBase().get(0), targetRelation, context.faction.getTag());
			context.faction.msg(Localization.COMMAND_RELATIONS_PROPOSAL_SENT, currentRelationColor + them.getTag(), "" + targetRelation.getColor() + targetRelation);
		}

		if(!targetRelation.isNeutral() && them.isPeaceful()) {
			them.msg(Localization.COMMAND_RELATIONS_PEACEFUL);
			context.faction.msg(Localization.COMMAND_RELATIONS_PEACEFULOTHER);
		}

		if(!targetRelation.isNeutral() && context.faction.isPeaceful()) {
			them.msg(Localization.COMMAND_RELATIONS_PEACEFULOTHER);
			context.faction.msg(Localization.COMMAND_RELATIONS_PEACEFUL);
		}

		FTeamWrapper.updatePrefixes(context.faction);
		FTeamWrapper.updatePrefixes(them);
	}

	private boolean hasMaxRelations(IFaction them, Relation targetRelation, CommandContext context) {
		if(FactionsPlugin.getInstance().conf().factions().maxRelations().isEnabled()) {
			int max = targetRelation.getMax();
			if(max != -1) {
				if(context.faction.getRelationCount(targetRelation) >= max) {
					context.msg(Localization.COMMAND_RELATIONS_EXCEEDS_ME, max, targetRelation.getPluralTranslation());
					return true;
				}
				if(them.getRelationCount(targetRelation) >= max) {
					context.msg(Localization.COMMAND_RELATIONS_EXCEEDS_THEY, max, targetRelation.getPluralTranslation());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_RELATIONS_DESCRIPTION;
	}
}
