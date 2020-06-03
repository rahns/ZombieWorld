package game;


import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
/**
 * An action for eating food.
 * @author ariehendrikse
 */
public class EatAction extends Action {
	
	private Food food;
	
	/**
	 * Constructor.
	 * 
	 * @param target the food to eat
	 */
	public EatAction(Item target) {
		try {
			if (!(target instanceof Food)) {
				throw new IllegalStateException("target must be an instance of Food.");
			}
			food = (Food) target;	
		}
		catch (IllegalStateException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Removes the food from the ground or inventory, then heals the eater.
	 * 
	 * @return String : description of who ate the food
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		boolean ateFromGround = false;
		for (Item item : map.locationOf(actor).getItems()) {
			if (item == food) {
				map.locationOf(actor).removeItem(item);
				ateFromGround = true;
				break;
			}
		}
		if (!ateFromGround) {
			actor.removeItemFromInventory(food);
		}
		actor.heal(food.getHealAmount());
		return actor.toString() + " ate some food and recovered " + food.getHealAmount() + " hit points";
	}

	@Override
	public String menuDescription(Actor actor) {
		return actor.toString() + " eats food";
	}

}
