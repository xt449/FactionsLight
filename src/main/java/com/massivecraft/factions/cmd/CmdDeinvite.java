package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class CmdDeinvite extends FCommand {

	public CmdDeinvite() {
		super();
		this.aliases.add("deinvite");
		this.aliases.add("deinv");

		this.optionalArgs.put("player", "player");
		//this.optionalArgs.put("", "");

		this.requirements = new CommandRequirements.Builder(Permission.DEINVITE)
				.memberOnly()
				.withAction(PermissibleAction.INVITE)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		IFactionPlayer you = context.argAsBestFPlayerMatch(0);
		if(you == null) {
			FancyMessage msg = new FancyMessage(Localization.COMMAND_DEINVITE_CANDEINVITE.toString()).color(ChatColor.GOLD);
			for(String id : context.faction.getInvites()) {
				IFactionPlayer fp = IFactionPlayerManager.getInstance().getById(id);
				String name = fp != null ? fp.getName() : id;
				msg.then(name + " ").color(ChatColor.WHITE).tooltip(Localization.COMMAND_DEINVITE_CLICKTODEINVITE.format(name)).command("/" + FactionsPlugin.getInstance().conf().getCommandBase().get(0) + " deinvite " + name);
			}
			context.sendFancyMessage(msg);
			return;
		}

		if(you.getFaction() == context.faction) {
			context.msg(Localization.COMMAND_DEINVITE_ALREADYMEMBER, you.getName(), context.faction.getTag());
			context.msg(Localization.COMMAND_DEINVITE_MIGHTWANT, FCmdRoot.getInstance().cmdKick.getUsageTemplate(context));
			return;
		}

		context.faction.deinvite(you);

		you.msg(Localization.COMMAND_DEINVITE_REVOKED, context.fPlayer.describeTo(you), context.faction.describeTo(you));

		context.faction.msg(Localization.COMMAND_DEINVITE_REVOKES, context.fPlayer.describeTo(context.faction), you.describeTo(context.faction));
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_DEINVITE_DESCRIPTION;
	}

}
