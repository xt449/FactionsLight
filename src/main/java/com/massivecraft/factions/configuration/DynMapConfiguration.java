package com.massivecraft.factions.configuration;

import com.massivecraft.factions.integration.dynmap.DynMapStyle;
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

		private final Set<Integer> visibleFactions = new HashSet<>();

		private final Set<Integer> hiddenFactions = new HashSet<>();

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

		public Set<Integer> getVisibleFactions() {
			return visibleFactions;
		}

		public Set<Integer> getHiddenFactions() {
			return hiddenFactions;
		}

		private final Map<Integer, Style> factionStyles = new HashMap<>();

		private transient Map<Integer, DynMapStyle> styles;

		public Map<Integer, DynMapStyle> getFactionStyles() {
			if(styles == null) {
				styles = new HashMap<>();
				for(Map.Entry<Integer, ?> e : ((Map<Integer, ?>) factionStyles).entrySet()) {
					Integer faction = e.getKey();
					Object s = e.getValue();
					if(s instanceof Style) {
						Style style = (Style) s;
						styles.put(faction, new DynMapStyle()
								.setLineColor(style.getLineColor())
								.setLineOpacity(style.getLineOpacity())
								.setLineWeight(style.getLineWeight())
								.setFillColor(style.getFillColor())
								.setFillOpacity(style.getFillOpacity())
//								.setHomeMarker(style.getHomeMarker())
								.setBoost(style.isStyleBoost()));
					} else if(s instanceof Map) {
						DynMapStyle style = new DynMapStyle();
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
//		@Setting
		private String lineColor = DynMapStyle.DEFAULT_LINE_COLOR;
		//		@Setting
		private final double lineOpacity = DynMapStyle.DEFAULT_LINE_OPACITY;
		//		@Setting
		private final int lineWeight = DynMapStyle.DEFAULT_LINE_WEIGHT;
		//		@Setting
		private String fillColor = DynMapStyle.DEFAULT_FILL_COLOR;
		//		@Setting
		private final double fillOpacity = DynMapStyle.DEFAULT_FILL_OPACITY;
		//		@Setting
		private final String homeMarker = DynMapStyle.DEFAULT_HOME_MARKER;
		//		@Setting
		private final boolean styleBoost = DynMapStyle.DEFAULT_BOOST;

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