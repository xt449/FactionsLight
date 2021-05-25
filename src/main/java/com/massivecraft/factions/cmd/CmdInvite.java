package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class CmdInvite extends FCommand {

	public CmdInvite() {
		super();
		this.aliases.add("invite");
		this.aliases.add("inv");

		this.requiredArgs.add("player");

		this.requirements = new CommandRequirements.Builder(Permission.INVITE)
				.memberOnly()
				.withAction(PermissibleAction.INVITE)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		IFactionPlayer target = context.argAsBestFPlayerMatch(0);
		if(target == null) {
			return;
		}

		if(target.getFaction() == context.faction) {
			context.msg(Localization.COMMAND_INVITE_ALREADYMEMBER, target.getName(), context.faction.getTag());
			context.msg(Localization.GENERIC_YOUMAYWANT + FCmdRoot.getInstance().cmdKick.getUsageTemplate(context));
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if(!context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostInvite(), Localization.COMMAND_INVITE_TOINVITE.toString(), Localization.COMMAND_INVITE_FORINVITE.toString())) {
			return;
		}

		if(context.faction.isBanned(target)) {
			context.msg(Localization.COMMAND_INVITE_BANNED, target.getName());
			return;
		}

		context.faction.invite(target);
		if(!target.isOnline()) {
			return;
		}

		// Tooltips, colors, and commands only apply to the string immediately before it.
		FancyMessage message = new FancyMessage(context.fPlayer.describeTo(target, true))
				.tooltip(Localization.COMMAND_INVITE_CLICKTOJOIN.toString())
				.command("/" + FactionsPlugin.getInstance().conf().getCommandBase().get(0) + " join " + context.faction.getTag())
				.then(Localization.COMMAND_INVITE_INVITEDYOU.toString())
				.color(ChatColor.YELLOW)
				.tooltip(Localization.COMMAND_INVITE_CLICKTOJOIN.toString())
				.command("/" + FactionsPlugin.getInstance().conf().getCommandBase().get(0) + " join " + context.faction.getTag())
				.then(context.faction.describeTo(target)).tooltip(Localization.COMMAND_INVITE_CLICKTOJOIN.toString())
				.command("/" + FactionsPlugin.getInstance().conf().getCommandBase().get(0) + " join " + context.faction.getTag());

		message.send(target.getPlayer());

		//you.msg("%s<i> invited you to %s",context.fPlayer.describeTo(you, true), context.faction.describeTo(you));
		context.faction.msg(Localization.COMMAND_INVITE_INVITED, context.fPlayer.describeTo(context.faction, true), target.describeTo(context.faction));
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_INVITE_DESCRIPTION;
	}

}
