package game;

import java.util.ArrayList;

import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;

/**
 * A class representing a shop
 * @author Rahn Stavar
 *
 */
public class Shop extends Ground {
	
	private ArrayList<Product> products;
	private String name;
	private int age = 0;
	private Actions shopActions = new Actions();
	private int unlockAge;

	/**
	 * Shop constructor
	 * @param name the name of the shop
	 * @param displayChar the display character of the shop
	 * @param products a list of the products this shop sells
	 */
	public Shop(String name, ArrayList<Product> products, int unlockAge) {
		super('[');
		this.products = products;
		this.name = name;
		this.unlockAge = unlockAge;
		this.shopActions.add(new DoNothingCustomMessageAction("stares at the closed " + name + " store"));
	}
	
	/**
	 * Tells the caller that an actor can not stand on this shop
	 * @param actor the actor who wants to stand on the shop
	 * 
	 * The actor parameter is ignored, and is only present to comply with Ground's public interface
	 * 
	 * @return false
	 */
	@Override
	public boolean canActorEnter(Actor actor) {
		return false;
	}
	
	/**
	 * A getter for this shop's allowable actions 
	 * @param actor the actor who wants to interact with the shop
	 * @param location the location of the shop
	 * @param direction a direction
	 * 
	 * The parameters are ignored and are only present to comply with Ground's public interface
	 * 
	 * @return the allowable actions at this shop
	 */
	@Override
	public Actions allowableActions(Actor actor, Location location, String direction) {
		return shopActions;
	}
	
	@Override
	public void tick(Location location) {  // Unlock the shop after a number of turns
		super.tick(location);
		if (age < unlockAge) {
			age++;
		}
		else if (age == unlockAge){
			shopActions.clear();
			shopActions.add(new ShopAction(products, name));
			displayChar = ']';
			age++;
		}
	}
}
