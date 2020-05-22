package game;

import java.util.List;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.PickUpItemAction;
/**
 * Class that determines how or whether a human can eat based on their inventory and the ground.
 */
public class EatBehaviour implements Behaviour {

	/**
	 * Method used check if there is food, then returns the EatAction or PickUpItemAction of the food.
	 * 
	 * @return Action to eat or pickup the food, with that food as the parameter.
	 */
	@Override
	public Action getAction(Actor actor, GameMap map) {
		List<Item> inventory = actor.getInventory();
		for (Item item : inventory) {
			if (item instanceof Food) {
				return new EatAction(item);
			}
		}
		for (Item item : map.locationOf(actor).getItems()) {
			if (item instanceof Food) {
				return new PickUpItemAction(item);
			}
		}
		return null;
	}

}
