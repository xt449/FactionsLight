package com.massivecraft.factions.cmd;

import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdPeaceful extends FCommand {

	public CmdPeaceful() {
		super();
		this.aliases.add("peaceful");

		this.requiredArgs.add("faction");

		this.requirements = new CommandRequirements.Builder(Permission.SET_PEACEFUL).build();
	}

	@Override
	public void perform(CommandContext context) {
		IFaction faction = context.argAsFaction(0);
		if(faction == null) {
			return;
		}

		String change;
		if(faction.isPeaceful()) {
			change = Localization.COMMAND_PEACEFUL_REVOKE.toString();
			faction.setPeaceful(false);
		} else {
			change = Localization.COMMAND_PEACEFUL_GRANT.toString();
			faction.setPeaceful(true);
		}

		// Inform all players
		for(IFactionPlayer fplayer : IFactionPlayerManager.getInstance().getOnlinePlayers()) {
			String blame = (context.fPlayer == null ? Localization.GENERIC_SERVERADMIN.toString() : context.fPlayer.describeTo(fplayer, true));
			if(fplayer.getFaction() == faction) {
				fplayer.msg(Localization.COMMAND_PEACEFUL_YOURS, blame, change);
			} else {
				fplayer.msg(Localization.COMMAND_PEACEFUL_OTHER, blame, change, faction.getTag(fplayer));
			}
		}

	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_PEACEFUL_DESCRIPTION;
	}

}
