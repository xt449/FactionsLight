package com.massivecraft.factions.configuration;

import com.massivecraft.factions.integration.dynmap.DynmapStyle;
import ninja.leaping.configurate.objectmapping.Setting;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"FieldCanBeLocal", "InnerClassMayBeStatic"})
public class DynMapConfiguration extends AbstractConfiguration {

	public DynMapConfiguration(Plugin plugin) {
		super(plugin, "dynmap.yml");
	}

	public class Dynmap {
		// Should the dynmap integration be used?
		private final boolean enabled = true;

		// Name of the Factions layer
		private final String layerName = "Factions";

		// Should the layer be visible per default
		private final boolean layerVisible = true;

		// Ordering priority in layer menu (low goes before high - default is 0)
		private final int layerPriority = 2;

		// (optional) set minimum zoom level before layer is visible (0 = default, always visible)
		private final int layerMinimumZoom = 0;

		// Format for popup - substitute values for macros
		private final String description = "<div class=\"infowindow\">\n"
				+ "<span style=\"font-weight: bold; font-size: 150%;\">%name%</span><br>\n"
				+ "<span style=\"font-style: italic; font-size: 110%;\">%description%</span><br>"
				+ "<br>\n"
				+ "<span style=\"font-weight: bold;\">Leader:</span> %players.leader%<br>\n"
				+ "<span style=\"font-weight: bold;\">Admins:</span> %players.admins.count%<br>\n"
				+ "<span style=\"font-weight: bold;\">Moderators:</span> %players.moderators.count%<br>\n"
				+ "<span style=\"font-weight: bold;\">Members:</span> %players.normals.count%<br>\n"
				+ "<span style=\"font-weight: bold;\">TOTAL:</span> %players.count%<br>\n"
				+ "</br>\n"
				+ "<span style=\"font-weight: bold;\">Bank:</span> %money%<br>\n"
				+ "<br>\n"
				+ "</div>";

		private final boolean visibilityByFaction = true;

		private final Set<String> visibleFactions = new HashSet<>();

		private final Set<String> hiddenFactions = new HashSet<>();

		public boolean isEnabled() {
			return enabled;
		}

		public String getLayerName() {
			return layerName;
		}

		public boolean isLayerVisible() {
			return layerVisible;
		}

		public int getLayerPriority() {
			return layerPriority;
		}

		public int getLayerMinimumZoom() {
			return layerMinimumZoom;
		}

		public String getDescription() {
			return description;
		}

		public boolean isVisibilityByFaction() {
			return visibilityByFaction;
		}

		public Set<String> getVisibleFactions() {
			return visibleFactions;
		}

		public Set<String> getHiddenFactions() {
			return hiddenFactions;
		}

		private final Map<String, Style> factionStyles = new HashMap<String, Style>() {
			{
				this.put("-1", new DynMapConfiguration.Style("#FF00FF", "#FF00FF"));
				this.put("-2", new DynMapConfiguration.Style("#FF0000", "#FF0000"));
			}
		};

		private transient Map<String, DynmapStyle> styles;

		public Map<String, DynmapStyle> getFactionStyles() {
			if(styles == null) {
				styles = new HashMap<>();
				for(Map.Entry<String, ?> e : ((Map<String, ?>) factionStyles).entrySet()) {
					String faction = e.getKey();
					Object s = e.getValue();
					if(s instanceof Style) {
						Style style = (Style) s;
						styles.put(faction, new DynmapStyle()
								.setLineColor(style.getLineColor())
								.setLineOpacity(style.getLineOpacity())
								.setLineWeight(style.getLineWeight())
								.setFillColor(style.getFillColor())
								.setFillOpacity(style.getFillOpacity())
//								.setHomeMarker(style.getHomeMarker())
								.setBoost(style.isStyleBoost()));
					} else if(s instanceof Map) {
						DynmapStyle style = new DynmapStyle();
						Map<String, Object> map = (Map<String, Object>) s;
//						if(map.containsKey("homeMarker")) {
//							style.setHomeMarker(map.get("homeMarker").toString());
//						}
						if(map.containsKey("fillOpacity")) {
							style.setFillOpacity(getDouble(map.get("fillOpacity").toString()));
						}
						if(map.containsKey("lineWeight")) {
							style.setLineWeight(getInt(map.get("lineWeight").toString()));
						}
						if(map.containsKey("lineColor")) {
							style.setLineColor(map.get("lineColor").toString());
						}
						if(map.containsKey("styleBoost")) {
							style.setBoost(Boolean.parseBoolean(map.get("styleBoost").toString()));
						}
						if(map.containsKey("fillColor")) {
							style.setFillColor(map.get("fillColor").toString());
						}
						if(map.containsKey("lineOpacity")) {
							style.setLineOpacity(getDouble(map.get("lineOpacity").toString()));
						}
						styles.put(faction, style);
					}  // Panic!

				}
			}
			return styles;
		}

		private int getInt(String s) {
			try {
				return Integer.parseInt(s);
			} catch(NumberFormatException ignored) {
				return 1;
			}
		}

		private double getDouble(String s) {
			try {
				return Double.parseDouble(s);
			} catch(NumberFormatException ignored) {
				return 1;
			}
		}
	}

	public class Style {
		// Region Style
		@Setting
		private String lineColor = DynmapStyle.DEFAULT_LINE_COLOR;
		@Setting
		private final double lineOpacity = DynmapStyle.DEFAULT_LINE_OPACITY;
		@Setting
		private final int lineWeight = DynmapStyle.DEFAULT_LINE_WEIGHT;
		@Setting
		private String fillColor = DynmapStyle.DEFAULT_FILL_COLOR;
		@Setting
		private final double fillOpacity = DynmapStyle.DEFAULT_FILL_OPACITY;
		@Setting
		private final String homeMarker = DynmapStyle.DEFAULT_HOME_MARKER;
		@Setting
		private final boolean styleBoost = DynmapStyle.DEFAULT_BOOST;

		private Style() {
			// Yay
		}

		private Style(String lineColor, String fillColor) {
			this.lineColor = lineColor;
			this.fillColor = fillColor;
		}

		public String getLineColor() {
			return lineColor;
		}

		public double getLineOpacity() {
			return lineOpacity;
		}

		public int getLineWeight() {
			return lineWeight;
		}

		public String getFillColor() {
			return fillColor;
		}

		public double getFillOpacity() {
			return fillOpacity;
		}

		public String getHomeMarker() {
			return homeMarker;
		}

		public boolean isStyleBoost() {
			return styleBoost;
		}
	}

	private final Dynmap dynmap = new Dynmap();
	private final Style style = new Style();

	public Dynmap dynmap() {
		return dynmap;
	}

	public Style style() {
		return style;
	}
}