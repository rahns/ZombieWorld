package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.PickUpItemAction;
import edu.monash.fit2099.engine.WeaponItem;

/**
 * A class that generates a PickupItemAction if there is a weapon at the actor's location.
 * @author Rahn Stavar
 *
 */
public class PickupWeaponBehaviour implements Behaviour{

	/**
	 * @param actor the actor who will pickup a weapon
	 * @param map the GameMap the weapon is located on
	 * @return a PickupItemAction for picking up a weapon at the actor's location
	 */
	@Override
	public Action getAction(Actor actor, GameMap map) {
		for (Item item : map.locationOf(actor).getItems()) {
			if (item instanceof WeaponItem && !(item instanceof DisablePickupWeaponBehaviour)) {
				return new PickUpItemAction(item);
			}
		}
		return null;
	}
	
}
