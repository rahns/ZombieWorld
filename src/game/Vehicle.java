package game;

import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.MoveActorAction;

public class Vehicle extends Item{
	
	public Vehicle(String name, char displayChar, Location destination, String destinationName) {
		super(name, displayChar, false);
		allowableActions.add(new MoveActorAction(destination, "into the " + name + " and rides to " + destinationName));
	}
	
	

}
