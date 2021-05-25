package com.massivecraft.factions.cmd;

import com.massivecraft.factions.IFaction;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

import java.util.ArrayList;
import java.util.List;

public class CmdBanlist extends FCommand {

	public CmdBanlist() {
		super();
		this.aliases.add("banlist");
		this.aliases.add("bans");
		this.aliases.add("banl");

		this.optionalArgs.put("faction", "faction");

		this.requirements = new CommandRequirements.Builder(Permission.BAN)
				.playerOnly()
				.build();
	}

	@Override
	public void perform(CommandContext context) {
		IFaction target = context.faction;
		if(!context.args.isEmpty()) {
			target = context.argAsFaction(0);
		}

		if(target == null) {
			context.msg(Localization.COMMAND_BANLIST_INVALID.format(context.argAsString(0)));
			return;
		}

		if(!target.isNormal()) {
			context.msg(Localization.COMMAND_BANLIST_NOFACTION);
			return;
		}

		List<String> lines = new ArrayList<>();
		lines.add(Localization.COMMAND_BANLIST_HEADER.format(target.getBannedPlayers().size(), target.getTag(context.faction)));
		int i = 1;

		for(BanInfo info : target.getBannedPlayers()) {
			IFactionPlayer banned = IFactionPlayerManager.getInstance().getById(info.getBanned());
			IFactionPlayer banner = IFactionPlayerManager.getInstance().getById(info.getBanner());
			String timestamp = Localization.sdf.format(info.getTime());

			lines.add(Localization.COMMAND_BANLIST_ENTRY.format(i, banned.getName(), banner.getName(), timestamp));
			i++;
		}

		for(String s : lines) {
			context.fPlayer.sendMessage(s);
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_BANLIST_DESCRIPTION;
	}
}
