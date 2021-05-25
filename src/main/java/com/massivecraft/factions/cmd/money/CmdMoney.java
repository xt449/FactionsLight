package com.massivecraft.factions.cmd.money;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.util.Localization;

public class CmdMoney extends MoneyCommand {

	public CmdMoney() {
		super();
		this.aliases.add("money");

		this.helpLong.add(plugin.txt().parseTags(Localization.COMMAND_MONEY_LONG.toString()));

		this.addSubCommand(new CmdMoneyBalance());
		this.addSubCommand(new CmdMoneyDeposit());
		this.addSubCommand(new CmdMoneyWithdraw());
		this.addSubCommand(new CmdMoneyTransferFf());
		this.addSubCommand(new CmdMoneyTransferFp());
		this.addSubCommand(new CmdMoneyTransferPf());
	}

	@Override
	public void perform(CommandContext context) {
		context.commandChain.add(this);
		FCmdRoot.getInstance().cmdAutoHelp.execute(context);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_MONEY_DESCRIPTION;
	}

}
