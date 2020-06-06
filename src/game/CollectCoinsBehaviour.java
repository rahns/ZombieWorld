package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

/**
 * A behaviour for picking up coins
 * @author Rahn Stavar
 *
 */
public class CollectCoinsBehaviour implements Behaviour {

	/**
	 * Gets an action to pickup the coins at an actors location if there are any
	 * @param actor the actor to pick up the coins
	 * @param map the map the actor is on
	 * @return a new CollectCoinsAction to pickup the coins
	 */
	@Override
	public Action getAction(Actor actor, GameMap map) {
		int numberOfCoins = 0;
		for (Item item : map.locationOf(actor).getItems()) {
			if (item instanceof Coin) {
				numberOfCoins++;
			}
		}
		if (numberOfCoins > 0) {
			return new CollectCoinsAction(numberOfCoins);
		}
		return null;
	}

}
