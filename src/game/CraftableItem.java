package game;

import edu.monash.fit2099.engine.Item;

/**
 * An interface which marks items as craftable.
 * Ensures any class which wants to be craftable has the required method(s). 
 * @author Rahn Stavar
 */
public interface CraftableItem {

	/**
	 * Creates and returns a new instance of the crafted item made from this item
	 * @return an Item
	 */
	public Item craftsInto();
	
}
