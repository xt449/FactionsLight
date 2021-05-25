package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import com.massivecraft.factions.util.TextUtil;

public class CmdTitle extends FCommand {

	public CmdTitle() {
		this.aliases.add("title");

		this.requiredArgs.add("player");
		this.optionalArgs.put("title", "title");

		this.requirements = new CommandRequirements.Builder(Permission.TITLE)
				.memberOnly()
				.withRole(Role.MODERATOR)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		IFactionPlayer you = context.argAsBestFPlayerMatch(0);
		if(you == null) {
			return;
		}

		context.args.remove(0);
		String title = TextUtil.implode(context.args, " ");

		title = title.replaceAll(",", "");

		if(!context.canIAdministerYou(context.fPlayer, you)) {
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if(!context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostTitle(), Localization.COMMAND_TITLE_TOCHANGE, Localization.COMMAND_TITLE_FORCHANGE)) {
			return;
		}

		you.setTitle(context.sender, title);

		// Inform
		context.faction.msg(Localization.COMMAND_TITLE_CHANGED, context.fPlayer.describeTo(context.faction, true), you.describeTo(context.faction, true));
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_TITLE_DESCRIPTION;
	}

}
