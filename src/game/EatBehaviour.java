package game;

import java.util.List;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;

public class EatBehaviour implements Behaviour {

	@Override
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
