package net.gasull.well.auction.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * The custom Inventory.
 */
public abstract class CustomInventory implements Inventory {

	/** The actual inventory. */
	private Inventory actualInventory;

	/** The player. */
	private Player player;

	/**
	 * Opens the actual inventory for the player.
	 */
	public void open() {
		player.openInventory(actualInventory);
	}

	/**
	 * Instantiates a new custom inventory.
	 * 
	 * @param player
	 *            the player
	 * @param type
	 *            the inventory type
	 * @param title
	 *            the inventory's title
	 */
	protected CustomInventory(Player player, InventoryType type, String title) {
		this.player = player;
		this.actualInventory = Bukkit.createInventory(player, type, title);
	}

	public HashMap<Integer, ItemStack> addItem(ItemStack... arg0) throws IllegalArgumentException {
		return actualInventory.addItem(arg0);
	}

	@SuppressWarnings("deprecation")
	public HashMap<Integer, ? extends ItemStack> all(int arg0) {
		return actualInventory.all(arg0);
	}

	public HashMap<Integer, ? extends ItemStack> all(Material arg0) throws IllegalArgumentException {
		return actualInventory.all(arg0);
	}

	public HashMap<Integer, ? extends ItemStack> all(ItemStack arg0) {
		return actualInventory.all(arg0);
	}

	public void clear() {
		actualInventory.clear();
	}

	public void clear(int arg0) {
		actualInventory.clear(arg0);
	}

	@SuppressWarnings("deprecation")
	public boolean contains(int arg0) {
		return actualInventory.contains(arg0);
	}

	public boolean contains(Material arg0) throws IllegalArgumentException {
		return actualInventory.contains(arg0);
	}

	public boolean contains(ItemStack arg0) {
		return actualInventory.contains(arg0);
	}

	@SuppressWarnings("deprecation")
	public boolean contains(int arg0, int arg1) {
		return actualInventory.contains(arg0, arg1);
	}

	public boolean contains(Material arg0, int arg1) throws IllegalArgumentException {
		return actualInventory.contains(arg0, arg1);
	}

	public boolean contains(ItemStack arg0, int arg1) {
		return actualInventory.contains(arg0, arg1);
	}

	public boolean containsAtLeast(ItemStack arg0, int arg1) {
		return actualInventory.contains(arg0, arg1);
	}

	@SuppressWarnings("deprecation")
	public int first(int arg0) {
		return actualInventory.first(arg0);
	}

	public int first(Material arg0) throws IllegalArgumentException {
		return actualInventory.first(arg0);
	}

	public int first(ItemStack arg0) {
		return actualInventory.first(arg0);
	}

	public int firstEmpty() {
		return actualInventory.firstEmpty();
	}

	public ItemStack[] getContents() {
		return actualInventory.getContents();
	}

	public InventoryHolder getHolder() {
		return actualInventory.getHolder();
	}

	public ItemStack getItem(int arg0) {
		return actualInventory.getItem(arg0);
	}

	public int getMaxStackSize() {
		return actualInventory.getMaxStackSize();
	}

	public String getName() {
		return actualInventory.getName();
	}

	public int getSize() {
		return actualInventory.getSize();
	}

	public String getTitle() {
		return actualInventory.getTitle();
	}

	public InventoryType getType() {
		return actualInventory.getType();
	}

	public List<HumanEntity> getViewers() {
		return actualInventory.getViewers();
	}

	public ListIterator<ItemStack> iterator() {
		return actualInventory.iterator();
	}

	public ListIterator<ItemStack> iterator(int arg0) {
		return actualInventory.iterator(arg0);
	}

	@SuppressWarnings("deprecation")
	public void remove(int arg0) {
		actualInventory.remove(arg0);
	}

	public void remove(Material arg0) throws IllegalArgumentException {
		actualInventory.remove(arg0);
	}

	public void remove(ItemStack arg0) {
		actualInventory.remove(arg0);
	}

	public HashMap<Integer, ItemStack> removeItem(ItemStack... arg0) throws IllegalArgumentException {
		return actualInventory.removeItem(arg0);
	}

	public void setContents(ItemStack[] arg0) throws IllegalArgumentException {
		actualInventory.setContents(arg0);
	}

	public void setItem(int arg0, ItemStack arg1) {
		actualInventory.setItem(arg0, arg1);
	}

	public void setMaxStackSize(int arg0) {
		actualInventory.setMaxStackSize(arg0);
	}

}
