package com.massivecraft.factions.cmd;

//public class CmdPower extends FCommand {
//
//	public CmdPower() {
//		super();
//		this.aliases.add("power");
//		this.aliases.add("pow");
//
//		this.optionalArgs.put("player", "you");
//
//		this.requirements = new CommandRequirements.Builder(Permission.POWER).noDisableOnLock().build();
//	}
//
//	@Override
//	public void perform(CommandContext context) {
//		FPlayer target = context.argAsBestFPlayerMatch(0, context.fPlayer);
//		if(target == null) {
//			return;
//		}
//
//		if(target != context.fPlayer && !Permission.POWER_ANY.has(context.sender, true)) {
//			return;
//		}
//
//		double powerBoost = target.getPowerBoost();
//		String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? TL.COMMAND_POWER_BONUS.toString() : TL.COMMAND_POWER_PENALTY.toString()) + powerBoost + ")";
//		context.msg(TL.COMMAND_POWER_POWER, target.describeTo(context.fPlayer, true), target.getPowerRounded(), target.getPowerMaxRounded(), boost);
//	}
//
//	@Override
//	public TL getUsageTranslation() {
//		return TL.COMMAND_POWER_DESCRIPTION;
//	}
//
//}
