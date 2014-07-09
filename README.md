well-auction
============

*Yet a simple but powerful way to set the economic equilibrium in your bukkit server: competitive auction houses.*

### Core features

* Create Well-Auction shops, from any block, item frame, and more to come.
* Sell item(s) from that house.
* Check items visually, with inventory-like window.
* Buy items, visually.
* Seller notification on each of his sales.
* **Localizable**. Don't hesitate to send here your translated configuration file in your language!
* DB storage from your bukkit server's configuration.
* Easy setup with ready-to-use shop presets.


### Basic use case

1. An administrator creates an auction house for a specific item.
2. From now on, players will be able to open the shop by right clicking it.
3. Players will be able to sell this item in this Well-Auction shop.
4. Sellers can define a price for each sale by shift-clicking it, or a default price for the item by shift-clicking the "Sell" button.
5. Once the price is defined, items on sale will be seen and buyable by other players in this Well-Auction shop.
6. Easy setup with ready-to-use shop presets.

Using this pattern on an entire room on autions for different items will be the dynamic hypermarket of your server!

### Fast and secure

* Well-Auction is **Performance and Transaction oriented**, because a performance-consuming plugin or an economy plugin with item duplication flaws is not meant to be really used.
* **No fictive item manipulation by the player**. As soon as the player has on his cursor an item that he doesn't really own, that means that would be a security breach for item duplication.
* The same actual Auction House will actually be shared across all shop instances of the sale, because at first it protects your players from losing items they put on sale.

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

* `\wellauction attach` to create or update an auction house by adding the type of item you're holding in your hand on sale. Creates the shop inside the block you're looking at (right-click to open).
* `\wellauction detach` to update an auction house by detaching the type of item you're holding in your hand from sales. A shop without sales is removed.
* `\wellauction preset` to attach a preset of items to an Auction House. Presets are configured in presets.yml. Creates the shop inside the block you're looking at (right-click to open).
* `\wellauction remove` to remove an auction house from the block you're looking at.
* `\wellauction list` lists the auction houses that exist.

### Permissions

* `well.auction` execute `/wellauction` commands
* `well.auction.open` open auction houses
* `well.auction.buy` buy items
* `well.auction.sell` sell items

# Links

* [~~well-auction's Wiki~~](https://github.com/blint6/well-auction/wiki):
  * ~~Configuration~~
  * ~~Permissions~~