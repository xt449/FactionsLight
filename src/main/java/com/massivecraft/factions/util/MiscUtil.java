package com.massivecraft.factions.util;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.perms.Role;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;

public class MiscUtil {
	public static final Function<String, EntityType> ENTITY_TYPE_FUNCTION;
	public static final Function<String, CreatureSpawnEvent.SpawnReason> SPAWN_REASON_FUNCTION;

	static {
		ENTITY_TYPE_FUNCTION = (string) -> string == null ? null : EntityType.valueOf(string.toUpperCase());
		SPAWN_REASON_FUNCTION = (string) -> string == null ? null : CreatureSpawnEvent.SpawnReason.valueOf(string.toUpperCase());
	}

	public static <Type> Set<Type> typeSetFromStringSet(Set<String> stringSet, Function<String, Type> function) {
		Set<Type> typeSet = new HashSet<>();
		for(String string : stringSet) {
			if(string != null) {
				Type item = function.apply(string);
				if(item != null) {
					typeSet.add(item);
				}
			}
		}
		return Collections.unmodifiableSet(typeSet);
	}

	// Inclusive range
	public static long[] range(long start, long end) {
		long[] values = new long[(int) Math.abs(end - start) + 1];

		if(end < start) {
			long oldstart = start;
			start = end;
			end = oldstart;
		}

		for(long i = start; i <= end; i++) {
			values[(int) (i - start)] = i;
		}

		return values;
	}

	/// TODO create tag whitelist!!
	public static final HashSet<String> substanceChars = new HashSet<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));

	public static String getComparisonString(String str) {
		StringBuilder ret = new StringBuilder();

		str = ChatColor.stripColor(str);
		str = str.toLowerCase();

		for(char c : str.toCharArray()) {
			if(substanceChars.contains(String.valueOf(c))) {
				ret.append(c);
			}
		}
		return ret.toString().toLowerCase();
	}

	public static ArrayList<String> validateFactionName(String str) {
		ArrayList<String> errors = new ArrayList<>();

		for(String blacklistItem : FactionsPlugin.getInstance().configMain.factions().limits().getNameBlacklist()) {
			if(str.toLowerCase().contains(blacklistItem.toLowerCase())) {
				errors.add(TextUtil.parse(TL.GENERIC_FACTIONTAG_BLACKLIST.toString()));
				break;
			}
		}

		if(getComparisonString(str).length() < FactionsPlugin.getInstance().configMain.factions().limits().getTagLengthMin()) {
			errors.add(TextUtil.parse(TL.GENERIC_FACTIONTAG_TOOSHORT.toString(), FactionsPlugin.getInstance().configMain.factions().limits().getTagLengthMin()));
		}

		if(str.length() > FactionsPlugin.getInstance().configMain.factions().limits().getTagLengthMax()) {
			errors.add(TextUtil.parse(TL.GENERIC_FACTIONTAG_TOOLONG.toString(), FactionsPlugin.getInstance().configMain.factions().limits().getTagLengthMax()));
		}

		if(!str.matches("^\\w+$")) {
			errors.add(TextUtil.parse(TL.GENERIC_FACTIONTAG_ALPHANUMERIC.toString(), str));
		}
		return errors;
	}

	public static Iterable<FPlayer> rankOrder(Iterable<FPlayer> players) {
		List<FPlayer> admins = new ArrayList<>();
		List<FPlayer> coleaders = new ArrayList<>();
		List<FPlayer> moderators = new ArrayList<>();
		List<FPlayer> normal = new ArrayList<>();
		List<FPlayer> recruit = new ArrayList<>();

		for(FPlayer player : players) {

			// Fix for some data being broken when we added the recruit rank.
			if(player.getRole() == null) {
				player.setRole(Role.NORMAL);
				FactionsPlugin.getInstance().log(Level.WARNING, String.format("Player %s had null role. Setting them to normal. This isn't good D:", player.getName()));
			}

			switch(player.getRole()) {
				case ADMIN:
					admins.add(player);
					break;

				case COLEADER:
					coleaders.add(player);
					break;

				case MODERATOR:
					moderators.add(player);
					break;

				case NORMAL:
					normal.add(player);
					break;

				case RECRUIT:
					recruit.add(player);
					break;
			}
		}

		List<FPlayer> ret = new ArrayList<>();
		ret.addAll(admins);
		ret.addAll(coleaders);
		ret.addAll(moderators);
		ret.addAll(normal);
		ret.addAll(recruit);
		return ret;
	}
}
