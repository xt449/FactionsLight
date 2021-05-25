package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class CmdMod extends FCommand {

	public CmdMod() {
		super();
		this.aliases.add("mod");
		this.aliases.add("setmod");
		this.aliases.add("officer");
		this.aliases.add("setofficer");

		this.optionalArgs.put("player", "player");

		this.requirements = new CommandRequirements.Builder(Permission.MOD)
				.memberOnly()
				.withRole(Role.COLEADER)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		IFactionPlayer you = context.argAsBestFPlayerMatch(0);
		if(you == null) {
			FancyMessage msg = new FancyMessage(Localization.COMMAND_MOD_CANDIDATES.toString()).color(ChatColor.GOLD);
			for(IFactionPlayer player : context.faction.getFPlayersWhereRole(Role.NORMAL)) {
				String s = player.getName();
				msg.then(s + " ").color(ChatColor.WHITE).tooltip(Localization.COMMAND_MOD_CLICKTOPROMOTE + s).command("/" + FactionsPlugin.getInstance().conf().getCommandBase().get(0) + " mod " + s);
			}

			context.sendFancyMessage(msg);
			return;
		}

		boolean permAny = Permission.MOD_ANY.has(context.sender, false);
		IFaction targetFaction = you.getFaction();

		if(targetFaction != context.faction && !permAny) {
			context.msg(Localization.COMMAND_MOD_NOTMEMBER, you.describeTo(context.fPlayer, true));
			return;
		}

		if(context.fPlayer != null && !context.fPlayer.getRole().isAtLeast(Role.COLEADER) && !permAny) {
			context.msg(Localization.COMMAND_MOD_NOTADMIN);
			return;
		}

		if(you == context.fPlayer && !permAny) {
			context.msg(Localization.COMMAND_MOD_SELF);
			return;
		}

		if(you.getRole() == Role.ADMIN) {
			context.msg(Localization.COMMAND_MOD_TARGETISADMIN);
			return;
		}

		if(you.getRole() == Role.MODERATOR) {
			// Revoke
			you.setRole(Role.NORMAL);
			targetFaction.msg(Localization.COMMAND_MOD_REVOKED, you.describeTo(targetFaction, true));
			context.msg(Localization.COMMAND_MOD_REVOKES, you.describeTo(context.fPlayer, true));
		} else {
			// Give
			you.setRole(Role.MODERATOR);
			targetFaction.msg(Localization.COMMAND_MOD_PROMOTED, you.describeTo(targetFaction, true));
			context.msg(Localization.COMMAND_MOD_PROMOTES, you.describeTo(context.fPlayer, true));
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_MOD_DESCRIPTION;
	}

}
