package game;

import edu.monash.fit2099.engine.Item;

/**
 * A class representing a store product. Each product has an item and a cost
 * @author Rahn Stavar
 *
 */
public class Product {
	
	private Item item;
	private int cost;
	
	/**
	 * Product constructor
	 * @param item the item that can be bought
	 * @param cost the cost of the item
	 */
	public Product(Item item, int cost) {
		this.cost = cost;
		this.item = item;
	}
	
	/**
	 * A getter for the products item 
	 * @return the item that can be bought
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * A getter for the product's price
	 * @return the price of the product
	 */
	public int getCost() {
		return cost;
	}

}
