package com.massivecraft.factions.config.transition.oldclass.v0;

import com.massivecraft.factions.FactionClaim;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.BanInfo;
import com.massivecraft.factions.util.LazyLocation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OldMemoryFactionV0 {
	protected String id = null;
	protected boolean peacefulExplosionsEnabled;
	protected boolean permanent;
	protected String tag;
	protected String description;
	protected boolean open;
	protected boolean peaceful;
	protected Integer permanentPower;
	protected LazyLocation home;
	protected long foundedDate;
	protected double money;
	protected double powerBoost;
	protected Map<String, Relation> relationWish = new HashMap<>();
	protected Map<FactionClaim, Set<String>> claimOwnership = new ConcurrentHashMap<>();
	protected Set<String> invites = new HashSet<>();
	protected HashMap<String, List<String>> announcements = new HashMap<>();
	protected ConcurrentHashMap<String, LazyLocation> warps = new ConcurrentHashMap<>();
	protected ConcurrentHashMap<String, String> warpPasswords = new ConcurrentHashMap<>();
	protected long lastDeath;
	protected int maxVaults;
	protected Role defaultRole;
	protected Map<OldPermissableV0, Map<OldPermissableActionV0, OldAccessV0>> permissions = new HashMap<>();
	protected Set<BanInfo> bans = new HashSet<>();

	private OldMemoryFactionV0() {
	}
}