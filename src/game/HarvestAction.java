package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
/*
 * abstract class for harvesting a crop. Will destroy the crop and replace it with dirt
 */
public abstract class HarvestAction extends Action {
	
	protected Location target;
	/**
	 * Constructor
	 * @param destination where the crop will be planted
	 */
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
