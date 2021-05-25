package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.LazyLocation;
import com.massivecraft.factions.util.Localization;

public class CmdSetWarp extends FCommand {

	public CmdSetWarp() {
		super();

		this.aliases.add("setwarp");
		this.aliases.add("sw");

		this.requiredArgs.add("warp");
		this.optionalArgs.put("password", "password");

		this.requirements = new CommandRequirements.Builder(Permission.SETWARP)
				.memberOnly()
				.withAction(PermissibleAction.SETWARP)
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		if(!(context.fPlayer.getRelationToLocation() == Relation.MEMBER)) {
			context.fPlayer.msg(Localization.COMMAND_SETFWARP_NOTCLAIMED);
			return;
		}

		int maxWarps = FactionsPlugin.getInstance().conf().commands().warp().getMaxWarps();
		if(maxWarps <= context.faction.getWarps().size()) {
			context.fPlayer.msg(Localization.COMMAND_SETFWARP_LIMIT, maxWarps);
			return;
		}

		if(!transact(context.fPlayer, context)) {
			return;
		}

		String warp = context.argAsString(0);
		String password = context.argAsString(1);

		LazyLocation loc = new LazyLocation(context.fPlayer.getPlayer().getLocation());
		context.faction.setWarp(warp, loc);
		if(password != null) {
			context.faction.setWarpPassword(warp, password);
		}
		context.fPlayer.msg(Localization.COMMAND_SETFWARP_SET, warp, password != null ? password : "");
	}

	private boolean transact(IFactionPlayer player, CommandContext context) {
		return player.isAdminBypassing() || context.payForCommand(FactionsPlugin.getInstance().conf().economy().getCostSetWarp(), Localization.COMMAND_SETFWARP_TOSET.toString(), Localization.COMMAND_SETFWARP_FORSET.toString());
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_SETFWARP_DESCRIPTION;
	}
}
