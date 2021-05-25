package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import com.massivecraft.factions.util.TextUtil;

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
		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if(!context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostDesc(), Localization.COMMAND_DESCRIPTION_TOCHANGE, Localization.COMMAND_DESCRIPTION_FORCHANGE)) {
			return;
		}

		// since "&" color tags seem to work even through plain old FPlayer.sendMessage() for some reason, we need to break those up
		// And replace all the % because it messes with string formatting and this is easy way around that.
		context.faction.setDescription(TextUtil.implode(context.args, " ").replaceAll("%", "").replaceAll("(&([a-f0-9klmnor]))", "& $2"));

		if(!FactionsPlugin.getInstance().conf().factions().chat().isBroadcastDescriptionChanges()) {
			context.fPlayer.msg(Localization.COMMAND_DESCRIPTION_CHANGED, context.faction.describeTo(context.fPlayer));
			context.fPlayer.sendMessage(context.faction.getDescription());
			return;
		}

		// Broadcast the description to everyone
		for(IFactionPlayer fplayer : IFactionPlayerManager.getInstance().getOnlinePlayers()) {
			fplayer.msg(Localization.COMMAND_DESCRIPTION_CHANGES, context.faction.describeTo(fplayer));
			fplayer.sendMessage(context.faction.getDescription());  // players can inject "&" or "`" or "<i>" or whatever in their description; &k is particularly interesting looking
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_DESCRIPTION_DESCRIPTION;
	}

}
