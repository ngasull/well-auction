well-auction
============

*Yet a simple but powerful way to set the economic equilibrium in your bukkit server: competitive auction houses.*

### Core features

* Create Well-Auction shops, from **any block, sign, item frame**, and more to come.
* Shops can handle **any kind and number of items** (limited to double chest inventory size).
* Allow players to **put on sale items** from their inventory to these shops.
* Check, buy and sell items **visually, with inventory-like window**.
* Seller **notification on each sale**.
* **Localizable** *Don't hesitate to send here your translated configuration file in your language!*
* **DB storage** from your bukkit server's configuration.
* **Easy and fast setup** thanks to ready-to-use shop presets.

### Fast and secure

* Well-Auction is **Performance and Transaction oriented**, because a performance-consuming plugin or an economy plugin with item duplication flaws is not meant to be really used.
* **No fictive item manipulation by the player**. As soon as the player has on his cursor an item that he doesn't really own, that means that would be a security breach for item duplication.
* The same actual Auction House will actually be **shared** across all shop instances of the sale, because at first it protects your players from losing items they put on sale.

## For players

1. Right click an Auction House: an inventory of item categories appears.
2. Left clicking a category opens the associated "Buy" inventory.
3. Right-clicking a category opens the associated "Sell" inventory.
4. Shift-clicking a category lets the user define a price per unit of the item.
5. In "Sell" inventory, user can sell 1, 4, 8, 16, 32 or 64 units of the selected item. A sale can be taken back by right-clicking it.
6. In "Buy" inventory, only best sales by stack size are shown. This is the core of the competitivity system!

## For server owners

### How to

1. Look at the desired block to be an Auction House, then load a shop here by typing `/wauc preset <preset_name>`
2. That's all! Just repeat the operation for all the shops you want to setup.
3. You can change the language in `well-auction.yml`, key: `language`. This will look for a the file named `<language>.yml` in *lang* subfolder.

### Presets

* building1
* building2
* building3
* glass
* decoration
* coloration
* primary1
* primary2
* mineral
* misc
* food1
* food2
* redstone
* tools1
* tools2
* fightwear
* craft
* harvesting
* vegetal
* rare

### To do

* [x] Multiple items to be sold in a single Well-Auction shop.
* [x] Players can unset price of sale, taking default dynamic price, if set.
* [x] Clean sales sorting for optimal visualization.
* [x] Item presets for quick shops setup.
* [ ] **Create Well-Auction shop from NPCs**.
* [ ] Competitive system with seller notification on new best offer from another player.
* [ ] Configurable (and optional) tax for seller on each sale.


## Installation note

This plugin depends on [well-core (click here)](http://dev.bukkit.org/bukkit-plugins/well-core/), **please get its last version or well-auction won't work**.


## Usage

### Commands

* `\wauc preset` to attach a preset of items to an Auction House. Presets are configured in presets.yml.
* `\wauc remove` to remove an auction house from the block you're looking at.
* `\wauc attach` to create or update an auction house by adding the type of item you're holding in your hand on sale.
* `\wauc detach` to update an auction house by detaching the type of item you're holding in your hand from sales. A shop without sales is removed.
* `\wauc reload` reloads the plugin. This may be useful in case on economy plugin dependency conflict which would break setup of the plugin.
* `\wauc list` lists the auction houses that exist. *(for dev purpose)*

### Permissions

* `well.auction` execute `/wellauction` commands
* `well.auction.open` open auction houses
* `well.auction.buy` buy items
* `well.auction.sell` sell items

# Links

* [~~well-auction's Wiki~~](https://github.com/blint6/well-auction/wiki):
  * ~~Configuration~~
  * ~~Permissions~~