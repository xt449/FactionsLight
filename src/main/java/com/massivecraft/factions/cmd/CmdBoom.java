package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdBoom extends FCommand {

	public CmdBoom() {
		super();
		this.aliases.add("noboom");
		this.aliases.add("explosions");
		this.aliases.add("toggleexplosions");

		this.optionalArgs.put("on/off", "flip");

		this.requirements = new CommandRequirements.Builder(Permission.NO_BOOM)
				.memberOnly()
				.withRole(Role.MODERATOR)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		if(!context.faction.isPeaceful()) {
			context.msg(Localization.COMMAND_BOOM_PEACEFULONLY);
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if(!context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostNoBoom(), Localization.COMMAND_BOOM_TOTOGGLE, Localization.COMMAND_BOOM_FORTOGGLE)) {
			return;
		}

		context.faction.setPeacefulExplosionsEnabled(context.argAsBool(0, !context.faction.getPeacefulExplosionsEnabled()));

		String enabled = context.faction.noExplosionsInTerritory() ? Localization.GENERIC_DISABLED.toString() : Localization.GENERIC_ENABLED.toString();

		// Inform
		context.faction.msg(Localization.COMMAND_BOOM_ENABLED, context.fPlayer.describeTo(context.faction), enabled);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_BOOM_DESCRIPTION;
	}
}
