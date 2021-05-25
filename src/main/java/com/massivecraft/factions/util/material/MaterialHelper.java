package com.massivecraft.factions.util.material;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Material;

import java.io.IOException;

public abstract class MaterialHelper {

	public static Material get(String name) {
		return Material.valueOf(name.toUpperCase());
	}

	public static Material get(String name, Material defaultMaterial) {
		try {
			return Material.valueOf(name.toUpperCase());
		} catch(IllegalArgumentException ignored) {
			return defaultMaterial;
		}
	}

	public static class MaterialAdapter extends TypeAdapter<Material> {

		@Override
		public void write(JsonWriter out, Material value) throws IOException {
			out.value(value.name());
		}

		@Override
		public Material read(JsonReader in) throws IOException {
			return MaterialHelper.get(in.nextString());
		}
	}
}
