package net.gasull.well.db;

import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;

/**
 * Common DAO for Well apps.
 */
public abstract class WellDao {

	/** The actual db object. */
	private final EbeanServer db;

	/**
	 * Instantiates a new well dao.
	 * 
	 * @param plugin
	 *            the plugin
	 */
	public WellDao(JavaPlugin plugin) {
		this.db = plugin.getDatabase();
	}

	/**
	 * Save.
	 * 
	 * @param model
	 *            the model
	 */
	public void save(Object model) {
		this.db.save(model);
	}

	/**
	 * Delete.
	 * 
	 * @param model
	 *            the model
	 */
	public void delete(Object model) {
		this.db.delete(model);
	}

	/**
	 * Refresh.
	 * 
	 * @param model
	 *            the model
	 */
	public void refresh(Object model) {
		db.refresh(model);
	}

}