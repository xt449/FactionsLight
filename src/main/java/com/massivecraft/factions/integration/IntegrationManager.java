package com.massivecraft.factions.integration;

import com.massivecraft.factions.FactionsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;

public class IntegrationManager implements Listener {
	private enum Integration {
		DYNMAP("dynmap", DynMapIntegration.getInstance()::init),
		LWC("LWC", LWCIntegration::setup),
		PLACEHOLDERAPI("PlaceholderAPI", (p) -> FactionsPlugin.getInstance().setupPlaceholderAPI()),
		@SuppressWarnings("Convert2MethodRef") SENTINEL("Sentinel", plugin -> SentinelIntegration.init(plugin)), // resist
		WORLDGUARD("WorldGuard", (plugin) -> {
			FactionsPlugin f = FactionsPlugin.getInstance();
			if(!f.configMain.worldGuard().isEnabled() && !f.configMain.worldGuard().isBuildPriority()) {
				return;
			}

			String version = plugin.getDescription().getVersion();
			if(version.startsWith("7")) {
				f.setWorldGuard(new Worldguard7Integration());
				f.getLogger().info("Found support for WorldGuard version " + version);
			} else {
				f.log(Level.WARNING, "Found WorldGuard but couldn't support this version: " + version);
			}
		});

		private static final Map<String, Consumer<Plugin>> STARTUP_MAP = new HashMap<>();

		static {
			for(Integration integration : values()) {
				STARTUP_MAP.put(integration.pluginName, integration.startup);
			}
		}

		static Consumer<Plugin> getStartup(String pluginName) {
			return STARTUP_MAP.getOrDefault(pluginName, plugin -> {
			});
		}

		private final String pluginName;
		private final Consumer<Plugin> startup;

		Integration(String pluginName, Consumer<Plugin> startup) {
			this.pluginName = pluginName;
			this.startup = startup;
		}
	}

	public IntegrationManager(FactionsPlugin plugin) {
		try {
			Field depGraph = SimplePluginManager.class.getDeclaredField("dependencyGraph");
			depGraph.setAccessible(true);
			Object graph = depGraph.get(plugin.getServer().getPluginManager());
			Method putEdge = graph.getClass().getDeclaredMethod("putEdge", Object.class, Object.class);
			putEdge.setAccessible(true);
			for(String depend : Integration.STARTUP_MAP.keySet()) {
				putEdge.invoke(graph, plugin.getDescription().getName(), depend);
			}
		} catch(Exception ignored) {
		}
		for(Integration integration : Integration.values()) {
			Plugin plug = plugin.getServer().getPluginManager().getPlugin(integration.pluginName);
			if(plug != null && plug.isEnabled()) {
				try {
					integration.startup.accept(plug);
				} catch(Exception e) {
					plugin.getLogger().log(Level.WARNING, "Failed to start " + integration.pluginName + " integration", e);
				}
			}
		}
	}

	@EventHandler
	public void onPluginEnabled(PluginEnableEvent event) {
		Integration.getStartup(event.getPlugin().getName()).accept(event.getPlugin());
	}
}
