package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Location;

public class Turret extends Gun implements DisablePickupWeaponBehaviour {
	
	private boolean isSetUp = false;
	private Display display;
	private SetUpTurretAction setUpAction;

	public Turret(Display display) {
		super("turret", '¬', 10, "shoots");
		this.display = display;
		this.setUpAction = new SetUpTurretAction(this);
		allowableActions.add(this.setUpAction);
	}
	
	@Override
	public void tick(Location currentLocation) {  // Called only when the item is on the ground
		if (isSetUp) {
			//Auto shoot someone
			display.println("A turret shoots");  //TODO: Replace string with an action that shoots someone
		}
	}
	
	@Override
	public void tick(Location currentLocation, Actor actor) {  // Called only when in an inventory
		isSetUp = false;
		displayChar = '¬';
		
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

	@Override
	public int shootDamage() {
		return 3;
	}

	@Override
	public ShootAction shoot() {
		return null;
	}
	

}
