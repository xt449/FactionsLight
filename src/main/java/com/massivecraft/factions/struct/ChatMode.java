package com.massivecraft.factions.struct;

import com.massivecraft.factions.util.Localization;

public enum ChatMode {
	MOD(4, Localization.CHAT_MOD),
	FACTION(3, Localization.CHAT_FACTION),
	ALLIANCE(2, Localization.CHAT_ALLIANCE),
	TRUCE(1, Localization.CHAT_TRUCE),
	PUBLIC(0, Localization.CHAT_PUBLIC);

	public final int value;
	public final Localization nicename;

	ChatMode(final int value, final Localization nicename) {
		this.value = value;
		this.nicename = nicename;
	}

	public boolean isAtLeast(ChatMode role) {
		return this.value >= role.value;
	}

	public boolean isAtMost(ChatMode role) {
		return this.value <= role.value;
	}

	@Override
	public String toString() {
		return this.nicename.toString();
	}

	public ChatMode getNext() {
		switch(this) {
			case PUBLIC:
				return TRUCE;
			case TRUCE:
				return ALLIANCE;
			case ALLIANCE:
				return FACTION;
			case FACTION:
				return MOD;
			default:
				return PUBLIC;
		}
	}
}
