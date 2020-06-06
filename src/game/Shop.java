package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;

public class Shop extends Ground {
	
	private ArrayList<Product> products;
	private String name;

	public Shop(String name, char displayChar, ArrayList<Product> products) {
		super(displayChar);
		this.products = products;
		this.name = name;
	}
	
	@Override
	public boolean canActorEnter(Actor actor) {
		return false;
	}
	
	@Override
	public Actions allowableActions(Actor actor, Location location, String direction) {
		Actions shopActions = new Actions();
		shopActions.add(new ShopAction(products, name));
		return shopActions;
	}
}
