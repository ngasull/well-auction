well-auction
============

*Yet a simple but powerful way to set the economic equilibrium in your bukkit server: competitive auction houses.*

### Core features

* Create Well-Auction shops, from any block, item frame, and more to come.
* Sell item(s) from that house.
* Check items visually, with inventory-like window.
* Buy items, visually.
* Seller notification on each of his sales.
* **Translatable**. Don't hesitate to send here your translated configuration file in your language!
* DB storage from your bukkit server's configuration.

### Basic use case

1. An administrator creates an auction house for a specific item.
2. From now on, players will be able to open the shop by right clicking it.
3. Players will be able to sell this item in this Well-Auction shop.
4. Sellers can define a price for each sale by shift-clicking it, or a default price for the item by shift-clicking the "Sell" button.
5. Once the price is defined, items on sale will be seen and buyable by other players in this Well-Auction shop.

Using this pattern on an entire room on autions for different items will be the dynamic hypermarket of your server!

### Fast and secure

* Well-Auction is **Performance and Transaction oriented**, because a performance-consuming plugin or an economy plugin with item duplication flaws is not meant to be really used.
* **No fictive item manipulation by the player**. As soon as the player has on his cursor an item that he doesn't really own, that means that would be a security breach for item duplication.
* The same actual Auction House will actually be shared across all shop instances of the sale, because at first it protects your players from losing items they put on sale.

### To do

* [ ] Multiple items to be sold in a single Well-Auction shop.
* [ ] **Create Well-Auction shop from NPCs**.
* [ ] Competitive system with seller notification on new best offer from another player.
* [ ] Configurable (and optional) tax for seller on each sale.
* [ ] Players can unset price of sale, taking default dynamic price, if set.
* [ ] Sorted and paginated sales for better visualization.
* [ ] Better and more native inventory manipulation.


## Usage

### Commands

* `\wellauction create` to create an auction house that sell the type of item you're holding in your hand. Creates the shop "in" the block you're looking at.
* `\wellauction list` lists the auction houses that exist.

### Permissions

* `well.auction` execute `/wellauction` commands
* `well.auction.buy` buy items
* `well.auction.sell` sell items

# Links

* [~~well-auction's Wiki~~](https://github.com/blint6/well-auction/wiki):
  * ~~Configuration~~
  * ~~Permissions~~