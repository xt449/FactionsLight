package com.massivecraft.factions.config.file;

import com.massivecraft.factions.config.annotation.Comment;
import com.massivecraft.factions.config.annotation.DefinedType;
import com.massivecraft.factions.config.annotation.WipeOnReload;
import com.massivecraft.factions.integration.dynmap.DynmapStyle;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"FieldCanBeLocal", "InnerClassMayBeStatic"})
public class DynmapConfig {
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
		private final String description =
				"<div class=\"infowindow\">\n"
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

		@Comment("Allow players in faction to see one another on Dynmap (only relevant if Dynmap has 'player-info-protected' enabled)")
		private final boolean visibilityByFaction = true;

		@Comment("If not empty, *only* listed factions (by name or ID) will be shown.\n" +
				"To show all factions in a world, use 'world:worldnamehere'")
		private final Set<String> visibleFactions = new HashSet<>();

		@Comment("To hide all factions in a world, use 'world:worldnamehere'")
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

		@Comment("Per-faction overrides")
		@DefinedType
		private final Map<String, Style> factionStyles = new HashMap<String, Style>() {
			{
				this.put("-1", new DynmapConfig.Style("#FF00FF", "#FF00FF"));
				this.put("-2", new DynmapConfig.Style("#FF0000", "#FF0000"));
			}
		};

		@WipeOnReload
		private transient Map<String, DynmapStyle> styles;

		public Map<String, DynmapStyle> getFactionStyles() {
			if(styles == null) {
				styles = new HashMap<>();
				for(Map.Entry<String, Style> e : factionStyles.entrySet()) {
					String faction = e.getKey();
					Style style = e.getValue();
					styles.put(faction, new DynmapStyle()
							.setLineColor(style.getLineColor())
							.setLineOpacity(style.getLineOpacity())
							.setLineWeight(style.getLineWeight())
							.setFillColor(style.getFillColor())
							.setFillOpacity(style.getFillOpacity())
							.setHomeMarker(style.getHomeMarker())
							.setBoost(style.isStyleBoost()));
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

	@ConfigSerializable
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