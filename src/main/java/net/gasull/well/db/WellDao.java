package net.gasull.well.db;

import java.util.Collection;

import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Transaction;

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
	 * Saves a model.
	 * 
	 * @param model
	 *            the model
	 */
	public void save(Object model) {
		this.db.save(model);
	}

	/**
	 * Saves a collection of models.
	 * 
	 * @param models
	 *            the models
	 */
	public void save(Collection<?> models) {
		this.db.save(models);
	}

	/**
	 * Delete.
	 * 
	 * @param model
	 *            the model
	 */
	public void delete(Object model) {
		refresh(model);
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

	/**
	 * Begin a new transaction.
	 * 
	 * @return the transaction
	 */
	public Transaction transaction() {
		return db.beginTransaction();

	}
}