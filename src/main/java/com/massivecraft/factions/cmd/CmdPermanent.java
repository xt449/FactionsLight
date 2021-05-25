package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;


public class CmdPermanent extends FCommand {

	public CmdPermanent() {
		super();
		this.aliases.add("permanent");

		this.requiredArgs.add("faction");

		this.requirements = new CommandRequirements.Builder(Permission.SET_PERMANENT).build();
	}

	@Override
	public void perform(CommandContext context) {
		IFaction faction = context.argAsFaction(0);
		if(faction == null) {
			return;
		}

		String change;
		if(faction.isPermanent()) {
			change = Localization.COMMAND_PERMANENT_REVOKE.toString();
			faction.setPermanent(false);
		} else {
			change = Localization.COMMAND_PERMANENT_GRANT.toString();
			faction.setPermanent(true);
		}

		FactionsPlugin.getInstance().log((context.fPlayer == null ? "A server admin" : context.fPlayer.getName()) + " " + change + " the faction \"" + faction.getTag() + "\".");

		// Inform all players
		for(IFactionPlayer fplayer : IFactionPlayerManager.getInstance().getOnlinePlayers()) {
			String blame = (context.fPlayer == null ? Localization.GENERIC_SERVERADMIN.toString() : context.fPlayer.describeTo(fplayer, true));
			if(fplayer.getFaction() == faction) {
				fplayer.msg(Localization.COMMAND_PERMANENT_YOURS, blame, change);
			} else {
				fplayer.msg(Localization.COMMAND_PERMANENT_OTHER, blame, change, faction.getTag(fplayer));
			}
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_PERMANENT_DESCRIPTION;
	}
}
