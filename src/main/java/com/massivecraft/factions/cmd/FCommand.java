package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public abstract class FCommand {
	public enum CommandVisibility {
		VISIBLE, // Visible commands are visible to anyone. Even those who don't have permission to use it or is of invalid sender type.
		SECRET, // Secret commands are visible only to those who can use the command. These commands are usually some kind of admin commands.
		INVISIBLE // Invisible commands are invisible to everyone, even those who can use the command.
	}

	public final FactionsPlugin plugin;

	// Command Aliases
	public final List<String> aliases;

	// Information on the args
	public final List<String> requiredArgs;
	public final LinkedHashMap<String, String> optionalArgs;

	// Requirements to execute this command
	public CommandRequirements requirements;

	public FCommand() {
		plugin = FactionsPlugin.getInstance();

		requirements = new CommandRequirements.Builder(null).build();

		this.subCommands = new ArrayList<>();
		this.aliases = new ArrayList<>();

		this.requiredArgs = new ArrayList<>();
		this.optionalArgs = new LinkedHashMap<>();

		this.helpShort = null;
		this.helpLong = new ArrayList<>();
		this.visibility = CommandVisibility.VISIBLE;
	}

	public abstract void perform(CommandContext context);

	public void execute(CommandContext context) {
		// Is there a matching sub command?
		if(context.args.size() > 0) {
			for(FCommand subCommand : this.subCommands) {
				if(subCommand.aliases.contains(context.args.get(0).toLowerCase())) {
					context.args.remove(0);
					context.commandChain.add(this);
					subCommand.execute(context);
					return;
				}
			}
		}

		if(!validCall(context)) {
			return;
		}

		if(!this.isEnabled(context)) {
			return;
		}

		perform(context);
	}

	public boolean validCall(CommandContext context) {
		return requirements.computeRequirements(context, true) && validArgs(context);
	}

	public boolean isEnabled(CommandContext context) {
		if(FactionsPlugin.getInstance().getLocked() && requirements.isDisableOnLock()) {
			context.msg("<b>Factions was locked by an admin. Please try again later.");
			return false;
		}
		return true;
	}

	public boolean validArgs(CommandContext context) {
		if(context.args.size() < this.requiredArgs.size()) {
			if(context.sender != null) {
				context.msg(TL.GENERIC_ARGS_TOOFEW);
				context.sender.sendMessage(this.getUsageTemplate(context));
			}
			return false;
		}

		if(context.args.size() > this.requiredArgs.size() + this.optionalArgs.size() && this.requirements.isErrorOnManyArgs()) {
			if(context.sender != null) {
				// Get the to many string slice
				List<String> theToMany = context.args.subList(this.requiredArgs.size() + this.optionalArgs.size(), context.args.size());
				context.msg(TL.GENERIC_ARGS_TOOMANY, String.join(" ", theToMany));
				context.sender.sendMessage(this.getUsageTemplate(context));
			}
			return false;
		}
		return true;
	}

	/*
		Subcommands
	 */
	public final List<FCommand> subCommands;

	public void addSubCommand(FCommand subCommand) {
		this.subCommands.add(subCommand);
	}

	/*
		Help
	 */
	public final List<String> helpLong;
	public final CommandVisibility visibility;

	private String helpShort;

	public void setHelpShort(String val) {
		this.helpShort = val;
	}

	public String getHelpShort() {
		if(this.helpShort == null) {
			return getUsageTranslation().toString();
		}

		return this.helpShort;
	}

	public abstract TL getUsageTranslation();

	/*
		Common Logic
	 */
	public List<String> getToolTips(FPlayer player) {
		List<String> lines = new ArrayList<>();
		for(String s : FactionsPlugin.getInstance().configMain.commands().toolTips().player()) {
			lines.add(ChatColor.translateAlternateColorCodes('&', Tag.parsePlain(player, s)));
		}
		return lines;
	}

	public List<String> getToolTips(Faction faction) {
		List<String> lines = new ArrayList<>();
		for(String s : FactionsPlugin.getInstance().configMain.commands().toolTips().faction()) {
			lines.add(ChatColor.translateAlternateColorCodes('&', Tag.parsePlain(faction, s)));
		}
		return lines;
	}

	/*
	Help and Usage information
 */
	public String getUsageTemplate(CommandContext context, boolean addShortHelp) {
		StringBuilder ret = new StringBuilder();
		ret.append(ChatColor.DARK_AQUA).append('/');

		for(FCommand fc : context.commandChain) {
			ret.append(String.join(",", fc.aliases)).append(' ');
		}

		ret.append(String.join(",", this.aliases));

		List<String> args = new ArrayList<>();

		for(String requiredArg : this.requiredArgs) {
			args.add("<" + requiredArg + ">");
		}

		for(Map.Entry<String, String> optionalArg : this.optionalArgs.entrySet()) {
			String val = optionalArg.getValue();
			if(val == null) {
				val = "";
			} else {
				val = "=" + val;
			}
			args.add("[" + optionalArg.getKey() + val + "]");
		}

		if(args.size() > 0) {
			ret.append(ChatColor.AQUA).append(' ').append(String.join(" ", args));
		}

		if(addShortHelp) {
			ret.append(' ').append(ChatColor.YELLOW).append(this.getHelpShort());
		}

		return ret.toString();
	}

	public String getUsageTemplate(CommandContext context) {
		return getUsageTemplate(context, false);
	}

}
