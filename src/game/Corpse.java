package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Location;

public class Corpse extends PortableItem {
	
	boolean wasHuman;
	GameMap map;
	int riseIn;
	Random rand;
	String oldName;

	public Corpse(String name, boolean wasHuman, GameMap map, String oldName) {
		super(name, '%');
		this.wasHuman = wasHuman;
		this.map = map;
		this.oldName = oldName;
		this.rand = new Random();
		this.riseIn = rand.nextInt(6) + 5; // Range is 5-10 inclusive
	}
	
	@Override
	// Corpse is on the ground
	public void tick(Location currentLocation) {
		this.tick(currentLocation, null);
	}
	
	@Override
	// Corpse is in an inventory
	public void tick(Location currentLocation, Actor actor) {
		if (wasHuman) {
			if (riseIn == 0) {
				riseFromTheDead(currentLocation, actor);
			}
			else {
				riseIn -= 1;
			}
		}
	}
	
	private void riseFromTheDead(Location currentLocation, Actor actor) {
		Zombie newZombie = new Zombie("Zombie " + oldName, map);
		boolean success = false;
		
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
	}
	
	private boolean addAtValidAdjacentLocation(Location centreLocation, Actor newZombie) {
		List<Exit> exits = centreLocation.getExits();
		List<Exit> validExits = new ArrayList<>();
		for (Exit exit : exits) {
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
