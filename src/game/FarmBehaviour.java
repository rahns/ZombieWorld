package game;

import java.util.Random;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;
/**
 * Class that determines how or whether a human can farm surroundings.
 */

public class FarmBehaviour extends HarvestBehaviour implements Behaviour {
	private static final int SOW_PROBABILITY = 33;
	private Random rand = new Random();
	private Ground ground;
	
	@Override
	/**
	 * Method used check if there is dirt or crops near the farmer,
	 * then returns the approriate action action of the food.
	 * 
	 * Order of priority is Sow>Harvest>Fertalise
	 * 
	 * @param actor that is farming
	 * @param map of the world for checking the locations
	 * @return Action either Harvest, Fertalise, or Sow a crop
	 */
	public Action getAction(Actor actor, GameMap map) {
		
		Location here = map.locationOf(actor);
		
		if (rand.nextInt(100)>SOW_PROBABILITY) {
			
			for (Exit exit : here.getExits()) {
				Location destination = exit.getDestination();
				ground = destination.getGround();
				if (ground instanceof Dirt) {
					return new SowAction(destination);
				}
				
			}
		}
		
		ground = here.getGround();
		
		//Check the ground farmer is standing on
		if (ground instanceof Crop) {
			Location toHarvest= super.firstHarvestable(actor,map);
			if (toHarvest != null) {
				return new FarmerHarvestAction(toHarvest);
			}
			else {
				return new FertaliseAction(here);
			}
		}	
		return null;

	}
}
