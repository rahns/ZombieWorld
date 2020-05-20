package game;

import java.util.List;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
/**
 * Class that determines how or whether a human can eat based on their inventory.
 */
public class EatBehaviour implements Behaviour {

	@Override
	/**
	 * Method used check if there is food, then returns the EatAction of the food.
	 * 
	 * @return Action to eat the food, with that food as the parameter.
	 */
	public Action getAction(Actor actor, GameMap map) {
		List<Item> inventory = actor.getInventory();
		for (Item item : inventory) {
			if (item instanceof Food) {
				return new EatAction(item);
			}
		}
		return null;
	}

}
