package com.massivecraft.factions.integration;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public interface IWorldguard {

	boolean isPVP(Player player);

	boolean playerCanBuild(Player player);

	boolean checkForRegionsInChunk(Chunk chunk);

}