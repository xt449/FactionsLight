package com.massivecraft.factions.cmd.dtr;

//public class CmdDTRGet extends FCommand {
//	public CmdDTRGet() {
//		super();
//		this.aliases.add("get");
//		this.optionalArgs.put("faction", "yours");
//
//		this.requirements = new CommandRequirements.Builder(Permission.DTR).noDisableOnLock().build();
//	}
//
//	@Override
//	public void perform(CommandContext context) {
//		Faction target = context.argAsFaction(0, context.faction);
//		if(target == null) {
//			return;
//		}
//
//		if(target != context.faction && !Permission.DTR_ANY.has(context.sender, true)) {
//			return;
//		}
//
//		DTRControl dtr = (DTRControl) FactionsPlugin.getInstance().getLandRaidControl();
//		context.msg(TL.COMMAND_DTR_DTR, target.describeTo(context.fPlayer, false), DTRControl.round(target.getDTR()), DTRControl.round(dtr.getMaxDTR(target)));
//	}
//
//	@Override
//	public TL getUsageTranslation() {
//		return TL.COMMAND_DTR_DESCRIPTION;
//	}
//
//}
