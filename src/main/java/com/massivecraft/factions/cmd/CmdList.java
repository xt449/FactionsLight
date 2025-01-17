package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Permission;
import com.massivecraft.factions.tag.Tag;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;


public class CmdList extends FCommand {

	public CmdList() {
		super();
		this.aliases.add("list");
		this.aliases.add("ls");

		this.optionalArgs.put("page", "1");

		this.requirements = new CommandRequirements.Builder(Permission.LIST).build();
	}

	@Override
	public void perform(CommandContext context) {
		ArrayList<Faction> factionList = Factions.getInstance().getAllFactions();
		factionList.remove(Factions.getInstance().getWilderness());

		// Sort by total followers first
		factionList.sort(this.compare(Faction::getFPlayers));

		// Then sort by how many members are online now
		factionList.sort(this.compare(f -> f.getFPlayersWhereOnline(true)));

		ArrayList<String> lines = new ArrayList<>();

		factionList.add(0, Factions.getInstance().getWilderness());

		final int pageheight = 9;
		int pagenumber = context.argAsInt(0, 1);
		int pagecount = (factionList.size() / pageheight) + 1;
		if(pagenumber > pagecount) {
			pagenumber = pagecount;
		} else if(pagenumber < 1) {
			pagenumber = 1;
		}
		int start = (pagenumber - 1) * pageheight;
		int end = start + pageheight;
		if(end > factionList.size()) {
			end = factionList.size();
		}


		String header = plugin.configMain.commands().list().header();
		String footer = plugin.configMain.commands().list().footer();

		if(!header.isEmpty()) {
			header = header.replace("{pagenumber}", String.valueOf(pagenumber)).replace("{pagecount}", String.valueOf(pagecount));
			lines.add(TextUtil.parse(header));
		}

		for(Faction faction : factionList.subList(start, end)) {
			if(faction.isWilderness()) {
				lines.add(TextUtil.parse(Tag.parsePlain(faction, plugin.configMain.commands().list().factionlessEntry())));
				continue;
			}
			lines.add(TextUtil.parse(Tag.parsePlain(faction, context.fPlayer, plugin.configMain.commands().list().entry())));
		}

		if(!footer.isEmpty()) {
			footer = footer.replace("{pagenumber}", String.valueOf(pagenumber)).replace("{pagecount}", String.valueOf(pagecount));
			lines.add(TextUtil.parse(footer));
		}

		context.sendMessage(lines);
	}

	private Comparator<Faction> compare(Function<Faction, ? extends Collection<?>> func) {
		return (f1, f2) -> {
			int f1Size = func.apply(f1).size();
			int f2Size = func.apply(f2).size();
			if(f1Size < f2Size) {
				return 1;
			} else if(f1Size > f2Size) {
				return -1;
			}
			return 0;
		};
	}

	@Override
	public TL getUsageTranslation() {
		return TL.COMMAND_LIST_DESCRIPTION;
	}
}