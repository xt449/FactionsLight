package com.massivecraft.factions.cmd;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.FactionRenameEvent;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;

import java.util.ArrayList;

public class CmdRename extends FCommand {

	public CmdRename() {
		this.aliases.add("rename");

		this.requiredArgs.add("faction tag");

		this.requirements = new CommandRequirements.Builder(Permission.TAG)
				.memberOnly()
				.withRole(Role.MODERATOR)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		String name = context.argAsString(0);

		// TODO does not first shouldCancel cover selfcase?
		if(Factions.getInstance().isTagTaken(name) && !MiscUtil.getComparisonString(name).equals(context.faction.getComparisonTag())) {
			context.msg(TL.COMMAND_RENAME_TAKEN);
			return;
		}

		ArrayList<String> errors = MiscUtil.validateFactionName(name);
		if(errors.size() > 0) {
			context.sendMessage(errors);
			return;
		}

		// trigger the faction rename event (cancellable)
		FactionRenameEvent renameEvent = new FactionRenameEvent(context.fPlayer, name);
		Bukkit.getServer().getPluginManager().callEvent(renameEvent);
		if(renameEvent.isCancelled()) {
			return;
		}

		String oldtag = context.faction.getTag();
		context.faction.setTag(name);

		// Inform
		for(FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
			if(fplayer.getFactionId() == context.faction.getId()) {
				fplayer.msg(TL.COMMAND_RENAME_FACTION, context.fPlayer.describeTo(context.faction, true), context.faction.getTag(context.faction));
				continue;
			}

			Faction faction = fplayer.getFaction();
			fplayer.msg(TL.COMMAND_RENAME_CHANGED, context.fPlayer.getColorTo(faction) + oldtag, context.faction.getTag(faction));
		}

		FTeamWrapper.updatePrefixes(context.faction);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_RENAME_DESCRIPTION;
	}

}
