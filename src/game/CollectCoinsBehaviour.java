package game;

import java.util.ArrayList;

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
		ArrayList<Coin> coins = new ArrayList<>();
		for (Item item : map.locationOf(actor).getItems()) {
			if (item instanceof Coin) {
				coins.add((Coin) item);
			}
		}
		if (coins.size() > 0) {
			return new CollectCoinsAction(coins);
		}
		return null;
	}

}
