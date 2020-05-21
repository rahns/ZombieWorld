package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public abstract class HarvestAction extends Action {
	
	protected Location target;

	public HarvestAction(Location destination) {
		this.target=destination;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		target.setGround(new Dirt());
		return actor.toString() + " harvested a crop";
	}

	@Override
	public String menuDescription(Actor actor) {
		return "Harvest Crop";
	}

}
