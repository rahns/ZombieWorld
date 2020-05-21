package game;


import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
/**
 * Class for regular human's Harvesting, where the time is added to the actors inventory
 */
public class HumanHarvestAction extends HarvestAction {
	/**
	 * Constructor 
	 * @param destination : Where the farmer harvest his crop
	 */
	public HumanHarvestAction(Location destination) {
		super(destination);
	}
	
	@Override
	/**
	 * Adds the food to the actor's inventory and reverts the ground to dirt.
	 * 
	 * @param actor : the actor to harvest
	 * @param map : the map to harvest on
	 * @return String : the message to be displayed when harvesting
	 */
	public String execute(Actor actor, GameMap map) {
		String message=super.execute(actor, map);
		actor.addItemToInventory(new Food());
		return message;
	}

}
