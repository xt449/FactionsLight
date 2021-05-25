package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdPowerBoost extends FCommand {

	public CmdPowerBoost() {
		super();
		this.aliases.add("powerboost");

		this.requiredArgs.add("p/f/player/faction");
		this.requiredArgs.add("name");
		this.requiredArgs.add("#/reset");

		this.requirements = new CommandRequirements.Builder(Permission.POWERBOOST).build();
	}

	@Override
	public void perform(CommandContext context) {
		String type = context.argAsString(0).toLowerCase();
		boolean doPlayer = true;
		if(type.equals("f") || type.equals("faction")) {
			doPlayer = false;
		} else if(!type.equals("p") && !type.equals("player")) {
			context.msg(Localization.COMMAND_POWERBOOST_HELP_1);
			context.msg(Localization.COMMAND_POWERBOOST_HELP_2);
			return;
		}

		Double targetPower = context.argAsDouble(2);
		if(targetPower == null) {
			if(context.argAsString(2).equalsIgnoreCase("reset")) {
				targetPower = 0D;
			} else {
				context.msg(Localization.COMMAND_POWERBOOST_INVALIDNUM);
				return;
			}
		}

		String target;

		if(doPlayer) {
			IFactionPlayer targetPlayer = context.argAsBestFPlayerMatch(1);
			if(targetPlayer == null) {
				return;
			}

			if(targetPower != 0) {
				targetPower += targetPlayer.getPowerBoost();
			}
			targetPlayer.setPowerBoost(targetPower);
			target = Localization.COMMAND_POWERBOOST_PLAYER.format(targetPlayer.getName());
		} else {
			IFaction targetFaction = context.argAsFaction(1);
			if(targetFaction == null) {
				return;
			}

			if(targetPower != 0) {
				targetPower += targetFaction.getPowerBoost();
			}
			targetFaction.setPowerBoost(targetPower);
			target = Localization.COMMAND_POWERBOOST_FACTION.format(targetFaction.getTag());
		}

		int roundedPower = (int) Math.round(targetPower);
		context.msg(Localization.COMMAND_POWERBOOST_BOOST, target, roundedPower);
		if(context.player != null) {
			FactionsPlugin.getInstance().log(Localization.COMMAND_POWERBOOST_BOOSTLOG.toString(), context.fPlayer.getName(), target, roundedPower);
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_POWERBOOST_DESCRIPTION;
	}
}
