package game;


import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
/**
 * Class for Farmer Harvesting, rather than the item going into inventory it instead goes on the ground
 * 
 * A farmer can sow, harvest, or fertalise a crop. They get one hoe each.
 */
public class FarmerHarvestAction extends HarvestAction {
	/**
	 * Constructor 
	 * @param destination : Where the farmer harvest his crop
	 */
	public FarmerHarvestAction(Location destination) {
		super(destination);
	}
	
	@Override
	/**
	 * Adds the food to the ground and reverts it to dirt.
	 * 
	 * @param actor : the actor to harvest
	 * @param map : the map to harvest on
	 * @return String : the message to be displayed when harvesting
	 */
	public String execute(Actor actor, GameMap map) {
		String message=super.execute(actor, map);
		target.addItem(new Food());
		return message;
	}

}
