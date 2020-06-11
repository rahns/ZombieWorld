package game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Exit;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Item;
import edu.monash.fit2099.engine.Location;
import edu.monash.fit2099.engine.NumberRange;

public class Turret extends Item {
	
	private boolean isSetUp = false;
	private Display display;
	private SetUpTurretAction setUpAction;
	private ArrayList<GameMap> maps;
	private static final int DAMAGE = 10;

	public Turret(Display display, ArrayList<GameMap> maps) {
		super("turret", 'x', true);
		this.display = display;
		this.setUpAction = new SetUpTurretAction(this);
		this.maps = maps;
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
	
	public void setUp() {
		isSetUp = true;
		allowableActions.remove(setUpAction);
		displayChar = 'X';
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
	
	private void shoot(Actor targetActor) {
		// Shoot the target
		if (targetActor != null) {
			GameMap currentMap = null;
			for (GameMap map : maps) {
				NumberRange xRange = map.getXRange();
				NumberRange yRange = map.getYRange();
				for (int x : xRange) {
					for (int y : yRange) {
						if (map.at(x, y).getItems().contains(this)) {
							currentMap = map;
						}
					}
				}
			}
			display.println("A turret shoots " + targetActor + " for " + DAMAGE + " damage");
			display.println(new AttackAction(targetActor).executeWithoutWeapon(currentMap, DAMAGE));
		}
	}
}
