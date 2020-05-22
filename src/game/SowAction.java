package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
/**
 * Action class for sowing a crop, will change the ground from dirt to crop.
 * @author ariehendrikse
 */
public class SowAction extends Action {

	private Location destination;

	/**
	 * SowAction Constructor
	 * @param destination the location to sow the crop
	 */
	public SowAction(Location destination) {
		this.destination=destination;
	}

	/**
	 * Executes the Action, which changes the ground from dirt to crop
	 * @return A description of the action that happened
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		try {
			destination.setGround(new Crop());
			return actor.toString() + " sowed a crop";
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Gets the menu description for this action
	 * @return the menu description
	 */
	@Override
	public String menuDescription(Actor actor) {
		return "Sow crop";
	}

}
