package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Permission;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.util.TL;

public class CmdDescription extends FCommand {

	public CmdDescription() {
		super();
		this.aliases.add("desc");
		this.aliases.add("description");

		this.requiredArgs.add("desc");

		this.requirements = new CommandRequirements.Builder(Permission.DESCRIPTION)
				.memberOnly()
				.withRole(Role.MODERATOR)
				.noErrorOnManyArgs()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		// since "&" color tags seem to work even through plain old FPlayer.sendMessage() for some reason, we need to break those up
		// And replace all the % because it messes with string formatting and this is easy way around that.
		context.faction.setDescription(String.join(" ", context.args).replaceAll("%", "").replaceAll("(&([a-f0-9klmnor]))", "& $2"));

		// Broadcast the description to everyone
		for(FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
			fplayer.msg(TL.COMMAND_DESCRIPTION_CHANGES, context.faction.describeTo(fplayer));
			fplayer.sendMessage(context.faction.getDescription());  // players can inject "&" or "`" or "<i>" or whatever in their description; &k is particularly interesting looking
		}
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_DESCRIPTION_DESCRIPTION;
	}

}
