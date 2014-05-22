package net.gasull.well.auction.conf;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.java.JavaPlugin;

public class WellMeta {
	
	JavaPlugin plugin;

	public WellMeta(JavaPlugin plugin) {
		this.plugin = plugin;
	}
	
	public <T> T getMeta(Metadatable metaObject, String key) {
		List<MetadataValue> metas = metaObject.getMetadata(key);
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getMetas(Metadatable metaObject, String key) {
		List<MetadataValue> metas = metaObject.getMetadata(key);
		List<T> castedMeta = new ArrayList<T>();
		
		for (MetadataValue meta : metas) {
			castedMeta.add((T) meta);
		}
		return null;
	}
}
