package game;

import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.MoveActorAction;

/**
 * A class representing a vehicle in the Zombie game. 
 * A vehicle provides a fast way of moving from one location to another.
 * @author Rahn Stavar
 *
 */
public class Vehicle extends Item{
	
	/**
	 * Vehicle constructor.
	 * @param name the name of the vehicle (e.g. "car")
	 * @param displayChar the display character of the vehicle
	 * @param destination the location that this actor who uses the vehicle is transported to
	 * @param destinationName the friendly name of the destination location for displaying on the player's menu (e.g. "town")
	 */
	public Vehicle(String name, char displayChar, Location destination, String destinationName) {
		super(name, displayChar, false);  // This item is non-portable
		// Add a move action to the allowable actions:
		allowableActions.add(new MoveActorAction(destination, "into the " + name + " and rides to " + destinationName));
	}
	
	

}
