package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.IFactionClaimManager;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdSaveAll extends FCommand {

	public CmdSaveAll() {
		super();
		this.aliases.add("saveall");
		this.aliases.add("save");

		this.requirements = new CommandRequirements.Builder(Permission.SAVE).noDisableOnLock().build();
	}

	@Override
	public void perform(CommandContext context) {
		IFactionPlayerManager.getInstance().forceSave(false);
		Factions.getInstance().forceSave(false);
		IFactionClaimManager.getInstance().forceSave(false);
		context.msg(TL.COMMAND_SAVEALL_SUCCESS);
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_SAVEALL_DESCRIPTION;
	}

}