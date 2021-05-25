package com.massivecraft.factions.integration.permcontext;

import com.massivecraft.factions.IFactionClaimManager;
import com.massivecraft.factions.FactionClaim;
import com.massivecraft.factions.IFactionPlayer;
import com.massivecraft.factions.IFactionPlayerManager;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default FactionsUUID contexts.
 */
public enum Contexts implements Context {
	TERRITORY_RELATION((player) ->
			IFactionPlayerManager.getInstance().getByPlayer(player).getRelationTo(IFactionClaimManager.getInstance().getFactionAt(new FactionClaim(player.getLocation()))).getNameInASet(),
			Arrays.stream(Relation.values()).map(relation -> relation.name().toLowerCase()).collect(Collectors.toSet())),
	ROLE_AT_LEAST((player) ->
	{
		IFactionPlayer p = IFactionPlayerManager.getInstance().getByPlayer(player);
		return p.hasFaction() ? p.getRole().getRoleNamesAtOrBelow() : Collections.emptySet();
	},
			Arrays.stream(Role.values()).map(role -> role.name().toLowerCase()).collect(Collectors.toSet())),
	ROLE_AT_MOST((player) ->
	{
		IFactionPlayer p = IFactionPlayerManager.getInstance().getByPlayer(player);
		return p.hasFaction() ? p.getRole().getRoleNamesAtOrAbove() : Collections.emptySet();
	},
			Arrays.stream(Role.values()).map(role -> role.name().toLowerCase()).collect(Collectors.toSet()));


	/**
	 * The FactionsUUID namespace. Should not be used by another plugin.
	 */
	public static final String FACTIONSUUID_NAMESPACE = "factionsuuid";

	private final Function<Player, Set<String>> function;
	private final String name;
	private final String namespacedName;
	private final Set<String> possibilities;

	Contexts(Function<Player, Set<String>> function, Set<String> possibilities) {
		this.function = function;
		this.name = this.name().toLowerCase().replace('_', '-');
		this.namespacedName = FACTIONSUUID_NAMESPACE + ':' + this.name;
		this.possibilities = Collections.unmodifiableSet(possibilities);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getNamespace() {
		return FACTIONSUUID_NAMESPACE;
	}

	@Override
	public String getNamespacedName() {
		return this.namespacedName;
	}

	@Override
	public Set<String> getPossibleValues() {
		return this.possibilities;
	}

	@Override
	public Set<String> getValues(Player player) {
		return this.function.apply(player);
	}
}
