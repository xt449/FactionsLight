package com.massivecraft.factions.cmd.dtr;

//public class CmdDTRResetAll extends FCommand {
//
//	public CmdDTRResetAll() {
//		super();
//		this.aliases.add("resetall");
//
//		this.requirements = new CommandRequirements.Builder(Permission.MODIFY_DTR).build();
//	}
//
//	@Override
//	public void perform(CommandContext context) {
//		if(context.fPlayer != null) {
//			return;
//		}
//
//		DTRControl dtr = (DTRControl) FactionsPlugin.getInstance().getLandRaidControl();
//		Factions.getInstance().getAllFactions().forEach(target -> target.setDTR(dtr.getMaxDTR(target)));
//		context.msg(TL.COMMAND_DTR_MODIFY_DONE, "EVERYONE", "MAX");
//	}
//
//	@Override
//	public TL getUsageTranslation() {
//		return TL.COMMAND_DTR_MODIFY_DESCRIPTION;
//	}
//}
