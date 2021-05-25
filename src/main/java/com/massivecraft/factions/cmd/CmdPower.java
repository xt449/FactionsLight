package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdPower extends FCommand {

	public CmdPower() {
		super();
		this.aliases.add("power");
		this.aliases.add("pow");

		this.optionalArgs.put("player", "you");

		this.requirements = new CommandRequirements.Builder(Permission.POWER).noDisableOnLock().build();
	}

	@Override
	public void perform(CommandContext context) {
		IFactionPlayer target = context.argAsBestFPlayerMatch(0, context.fPlayer);
		if(target == null) {
			return;
		}

		if(target != context.fPlayer && !Permission.POWER_ANY.has(context.sender, true)) {
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if(!context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostPower(), Localization.COMMAND_POWER_TOSHOW, Localization.COMMAND_POWER_FORSHOW)) {
			return;
		}

		double powerBoost = target.getPowerBoost();
		String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? Localization.COMMAND_POWER_BONUS.toString() : Localization.COMMAND_POWER_PENALTY.toString()) + powerBoost + ")";
		context.msg(Localization.COMMAND_POWER_POWER, target.describeTo(context.fPlayer, true), target.getPowerRounded(), target.getPowerMaxRounded(), boost);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_POWER_DESCRIPTION;
	}

}
