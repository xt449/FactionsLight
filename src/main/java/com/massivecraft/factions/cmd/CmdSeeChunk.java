package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;
import com.massivecraft.factions.util.SeeChunkUtil;

public class CmdSeeChunk extends FCommand {

	private final boolean useParticles;

	public CmdSeeChunk() {
		super();
		this.aliases.add("seechunk");
		this.aliases.add("sc");

		this.requirements = new CommandRequirements.Builder(Permission.SEECHUNK)
				.playerOnly()
				.build();

		useParticles = FactionsPlugin.getInstance().conf().commands().seeChunk().isParticles();
	}

	@Override
	public void perform(CommandContext context) {
		if(useParticles) {
			boolean toggle = false;
			if(context.args.size() == 0) {
				toggle = !context.fPlayer.isSeeingChunk();
			} else if(context.args.size() == 1) {
				toggle = context.argAsBool(0);
			}
			context.fPlayer.setSeeingChunk(toggle);
			context.msg(Localization.COMMAND_SEECHUNK_TOGGLE, toggle ? "enabled" : "disabled");
		} else {
			SeeChunkUtil.showPillars(context.player, context.fPlayer, null, false);
		}
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_SEECHUNK_DESCRIPTION;
	}

}
