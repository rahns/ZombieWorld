package game;


import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;
/**
 * Class for Farmer Harvesting, rather than the item going into inventory it instead goes on the ground
 */
public class FarmerHarvestAction extends HarvestAction {
	
	private Location target;
	
	/**
	 * Constructor 
	 * @param destination : Where the farmer harvest his crop
	 */
	public FarmerHarvestAction(Location destination) {
		super(destination);
		this.target = destination;
	}
	
	/**
	 * Adds the food to the ground and reverts it to dirt.
	 * 
	 * @param actor : the actor to harvest
	 * @param map : the map to harvest on
	 * @return String : the message to be displayed when harvesting
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		String message=super.execute(actor, map);
		target.addItem(new Food());
		return message;
	}

}
