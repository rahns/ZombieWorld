package game;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public class PlayerHarvestAction extends HarvestAction {

	public PlayerHarvestAction(Location destination) {
		super(destination);
	}
	
	@Override
	public String execute(Actor actor, GameMap map) {
		String message=super.execute(actor, map);
		actor.addItemToInventory(food);
		return message;
	}

}
