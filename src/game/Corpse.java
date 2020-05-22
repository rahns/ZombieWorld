package game;

import java.util.ArrayList;

import java.util.List;
import java.util.Random;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

/**
 * A Corpse. Created by AttackAction when an actor dies.
 * If the corpse was human and killed by a zombie, it will turn into a zombie in 5-10 turns
 * 
 * @author Rahn Stavar
 */
public class Corpse extends PortableItem {
	
	private boolean shouldRise;  // True if the corpse was a human and was killed by a zombie
	private GameMap map;
	private int riseIn;  // A counter to count-down the turns until a human corpse should rise as a zombie
	private Random rand;
	private String oldName; // The corpse's old name

	/**
	 * Corpse constructor
	 * @param name the name of the dead actor
	 * @param shouldRise {@code true} if the corpse should rise into a zombie, {@code false} otherwise
	 * @param map the instance of GameMap that has this corpse
	 */
	public Corpse(String name, boolean shouldRise, GameMap map) {
		super("dead " + name, '%');
		try {
			this.shouldRise = shouldRise;
			this.map = map;
			this.oldName = name;
			this.rand = new Random();
			this.riseIn = rand.nextInt(6) + 5; // Range is 5-10 inclusive
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Inform a Corpse on the ground of the passage of time. 
     * It always calls the other tick(Location, Actor), passing it its currentLocation parameter
     * 
     * This method is called once per turn, if the item rests upon the ground.
     * @param currentLocation The location of the ground on which the limb lies.
     */
	@Override
	// Corpse is on the ground
	public void tick(Location currentLocation) {
		this.tick(currentLocation, null);
	}
	
    /**
     * Inform a carried Corpse of the passage of time.
     * Will make the corpse rise from the dead (if it should rise) after 5-10 turns have passed
     * 
     * This method is called once per turn, if the Item is being carried.
     * @param currentLocation The location of the actor carrying this Item.
     * @param actor The actor carrying this Item.
     */
	@Override
	// Corpse is in an inventory
	public void tick(Location currentLocation, Actor actor) {
		if (shouldRise) {
			if (riseIn == 0) {
				riseFromTheDead(currentLocation, actor);
			}
			else {
				riseIn--; // Decrement
			}
		}
	}
	//TODO documentation
	private void riseFromTheDead(Location currentLocation, Actor actor) {
		Zombie newZombie = new Zombie("Zombie " + oldName, map);
		boolean success;
		
		// If new zombie can be put at the current location (no other actor here and the ground permits it)
		if (currentLocation.canActorEnter(newZombie)) {
			currentLocation.addActor(newZombie);
			success = true;
		}
		// New zombie can not enter this location (could be because its corpse is in an inventory so an
		// actor is already at this location), so put in a random adjacent location:
		else {
			success = addAtValidAdjacentLocation(currentLocation, newZombie);
		}
		
		if (success) {
			// If corpse was in an inventory:
			if (actor != null) {
				// remove it:
				actor.removeItemFromInventory(this);
			}
			// If corpse was on the ground:
			else {
				currentLocation.removeItem(this);
			}
			System.out.println(oldName + " rose from the dead as a zombie");
		}
		// If rising was unsuccessful (due to no valid location for the new zombie), riseFromTheDead
		// will be called again next turn as riseIn still equals 0.
	}
	//TODO documentation
	private boolean addAtValidAdjacentLocation(Location centreLocation, Actor newZombie) {
		List<Exit> validExits = new ArrayList<>();
		
		for (Exit exit : centreLocation.getExits()) {
			// If actor can not be placed in the selected exit's location:
			if (exit.getDestination().canActorEnter(newZombie)) {
				// Remove it from the list:
				validExits.add(exit);
			}
		}
		// Place the new zombie:
		if (validExits.size() != 0) {
			// A valid exit was found, so add the new zombie:
			validExits.get(rand.nextInt(validExits.size())).getDestination().addActor(newZombie);
			
			// Adding the new zombie was successful
			return true;
		}
		// Adding the new zombie was unsuccessful
		return false;
	}

}
