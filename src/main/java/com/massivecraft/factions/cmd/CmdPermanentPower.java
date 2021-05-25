package com.massivecraft.factions.cmd;

import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdPermanentPower extends FCommand {
	public CmdPermanentPower() {
		super();
		this.aliases.add("permanentpower");

		this.requiredArgs.add("faction");
		this.requiredArgs.add("power");

		this.requirements = new CommandRequirements.Builder(Permission.SET_PERMANENTPOWER).build();
	}

	@Override
	public void perform(CommandContext context) {
		IFaction targetFaction = context.argAsFaction(0);
		if(targetFaction == null) {
			return;
		}

		Integer targetPower = context.argAsInt(1);

		targetFaction.setPermanentPower(targetPower);

		String change = Localization.COMMAND_PERMANENTPOWER_REVOKE.toString();
		if(targetFaction.hasPermanentPower()) {
			change = Localization.COMMAND_PERMANENTPOWER_GRANT.toString();
		}

		// Inform sender
		context.msg(Localization.COMMAND_PERMANENTPOWER_SUCCESS, change, targetFaction.describeTo(context.fPlayer));

		// Inform all other players
		for(IFactionPlayer fplayer : targetFaction.getFPlayersWhereOnline(true)) {
			if(fplayer == context.fPlayer) {
				continue;
			}
			String blame = (context.fPlayer == null ? Localization.GENERIC_SERVERADMIN.toString() : context.fPlayer.describeTo(fplayer, true));
			fplayer.msg(Localization.COMMAND_PERMANENTPOWER_FACTION, blame, change);
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_PERMANENTPOWER_DESCRIPTION;
	}
}
