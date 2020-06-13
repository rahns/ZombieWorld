package game;

import java.util.LinkedList;
import java.util.Queue;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;

/**
 * A class representing a turret
 * @author Rahn Stavar
 *
 */
public class Turret extends Item {
	
	private boolean isSetUp = false;
	private Display display;
	private SetUpTurretAction setUpAction;
	private GameMap currentMap;
	private static final int DAMAGE = 10;

	/**
	 * Turret constructor
	 * @param display the display to print the turrets shoot messages to
	 * @param maps the game's maps, so that the turret can remove any characters it kills from the game
	 */
	public Turret(Display display) {
		super("turret", 'x', true);
		this.display = display;
		this.setUpAction = new SetUpTurretAction(this);
		allowableActions.add(this.setUpAction);
	}
	
	@Override
	public void tick(Location currentLocation) {  // Called only when the item is on the ground
		if (isSetUp) {
			//Auto shoot someone
			shoot(findNearestTarget(currentLocation, ZombieCapability.UNDEAD));
		}
	}
	
	@Override
	public void tick(Location currentLocation, Actor actor) {  // Called only when in an inventory
		isSetUp = false;
		displayChar = 'x';
		
		boolean foundAction = false;
		for (Action i : allowableActions) {
			if (i == setUpAction) {
				foundAction = true;
			}
		}
		if (!foundAction) {
			allowableActions.add(setUpAction);
		}
		
	}
	
	/**
	 * A method to tell the turret to set up and start shooting
	 */
	public void setUp(GameMap map) {
		isSetUp = true;
		allowableActions.remove(setUpAction);
		displayChar = 'X';
		this.currentMap = map;
	}
	
	private Actor findNearestTarget(Location currentLocation, Enum<?> targetTeam) {
		// Find a target
		Actor targetActor = null;
		Queue<Location> queue = new LinkedList<>();
		queue.add(currentLocation);
		while (!queue.isEmpty()) {
			currentLocation = queue.poll();
			if (currentLocation.getActor() != null && currentLocation.getActor().hasCapability(targetTeam)) {
				targetActor = currentLocation.getActor();
				break;
			}
			else {
				for (Exit i : currentLocation.getExits()) {
					if (! queue.contains(i.getDestination())) {
						queue.add(i.getDestination());
					}
				}
			}
		}
		return targetActor;
	}
	
	/**
	 * Private method that shoots the nearest target
	 * @param targetActor
	 */
	private void shoot(Actor targetActor) {
		// Shoot the target
		if (targetActor != null) {
			display.println("A turret shoots " + targetActor + " for " + DAMAGE + " damage");
			display.println(new AttackAction(targetActor).executeAsItem(currentMap, DAMAGE));
		}
	}
}
