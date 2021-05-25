package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Localization;

public class CmdReload extends FCommand {

	public CmdReload() {
		super();
		this.aliases.add("reload");

		this.requirements = new CommandRequirements.Builder(Permission.RELOAD).noDisableOnLock().build();
	}

	@Override
	public void perform(CommandContext context) {
		long timeInitStart = System.currentTimeMillis();
		FactionsPlugin.getInstance().getConfigManager().loadConfigs();
		FactionsPlugin.getInstance().reloadConfig();
		FactionsPlugin.getInstance().loadLang();
		long timeReload = (System.currentTimeMillis() - timeInitStart);

		context.msg(Localization.COMMAND_RELOAD_TIME, timeReload);
	}

	@Override
	public Localization getUsageTranslation() {
		return Localization.COMMAND_RELOAD_DESCRIPTION;
	}
}
