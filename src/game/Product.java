package game;

import edu.monash.fit2099.engine.Item;

public class Product {
	
	private Item item;
	private int cost;
	
	public Product(Item item, int cost) {
		this.cost = cost;
		this.item = item;
	}
	
	public Item getItem() {
		return item;
	}
	
	public int getCost() {
		return cost;
	}

}
