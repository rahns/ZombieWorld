package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

/**
 * An action for crafting a {@code CraftableItem} into a different item
 * 
 * @author Rahn Stavar
 */
public class CraftAction extends Action{
	
	private Item item; 
	private Item newItem;
	
	/**
	 * CraftAction constructor
	 * @param item a {@code CraftableItem} to be crafted into something else
	 * @throws IllegalStateException when the parameter {@code item} is not an instance of {@code Item}
	 */
	public CraftAction(CraftableItem item) {
		// item must be an instance of Item as well as implement CraftableItem
		try {
			if (!(item instanceof Item)) {
				throw new IllegalStateException("CraftableItems must also be an instance of Item.");
			}
			this.item = (Item) item;
			this.newItem = item.craftsInto();
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Perform the Action.
	 *
	 * @param actor The actor performing the action.
	 * @param map The map the actor is on.
	 * @return a description of what happened that can be displayed to the user.
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		actor.addItemToInventory(newItem);
		if (map.locationOf(actor).getItems().contains(item)) {
			map.locationOf(actor).removeItem(item);
		}
		else {
			actor.removeItemFromInventory(item);
		}
		return actor.toString() + " crafted " + item.toString() + " into " + newItem.toString() + ".";
	}

	/**
	 * Returns a descriptive string
	 * @param actor The actor performing the action.
	 * @return the text we put on the menu
	 */
	@Override
	public String menuDescription(Actor actor) {
		return "Craft " + item.toString() + " into " + newItem.toString();
	}

}
