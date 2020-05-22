package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
/*
 * abstract class for harvesting a crop. Will destroy the crop and replace it with dirt
 */
public abstract class HarvestAction extends Action {
	
	private Location target;
	
	/**
	 * Constructor
	 * @param destination where the crop will be planted
	 */
	public HarvestAction(Location destination) {
		this.target=destination;
	}

	/**
	 * Executes the Action, which replaces a crop with dirt
	 * @return A description of the action that happened
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		target.setGround(new Dirt());
		return actor.toString() + " harvested a crop";
	}

	/**
	 * Gets the menu description for this action
	 * @return the menu description
	 */
	@Override
	public String menuDescription(Actor actor) {
		return "Harvest Crop";
	}

}
