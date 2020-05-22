package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;

/**
 * A behaviour for harvesting ripe crops and placing them in the actor's inventory
 * @author ariehenrikse
 *
 */
public class HarvestBehaviour implements Behaviour {
	
	/**
	 * Constructor
	 */
	public HarvestBehaviour() {
	}
	
	/**
	 * Returns an action to harvest a crop and place it in the actor's inventory
	 */
	@Override
	public Action getAction(Actor actor, GameMap map) {	
		
		Location toHarvest = (firstHarvestable(actor,map));
		
		if (toHarvest!=null) {
			return new HumanHarvestAction(toHarvest);
		}
		
		return null;
	}
	
	/**
	 * Returns the first crop that is harvestable by iterating over the ground
	 * the actor is standing on then the surrounding exits.
	 * @param actor the actor harvesting
	 * @param map the gamemap
	 * @return the Location to harvest, if any.
	 */
	protected Location firstHarvestable(Actor actor, GameMap map) {
		Location here = map.locationOf(actor);
		Ground ground =here.getGround();
		if (ground instanceof Crop) {
			if ((((Crop) ground).isRipe())) {
				return here;
			} 
		}
		for (Exit exit : here.getExits()) {
			Location destination = exit.getDestination();
			ground = destination.getGround();
			if (ground instanceof Crop){
				if ((((Crop) ground).isRipe())) {
					return destination;
				} 
				
			}
			
		}
		return null;
	}
}
