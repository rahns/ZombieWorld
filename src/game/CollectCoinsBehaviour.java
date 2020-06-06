package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

public class CollectCoinsBehaviour implements Behaviour {

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
