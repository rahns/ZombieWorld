package game;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Location;

public class Turret extends Gun {
	
	private boolean isSetUp = false;
	private Display display;

	public Turret(Display display) {
		super("turret", '¬', 10, "shoots");
		this.display = display;
		allowableActions.add(new SetUpTurretAction(this));
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
		if (allowableActions.size() == 0) {
			allowableActions.add(new SetUpTurretAction(this));
		}
		
	}
	
	public void setUp() {
		isSetUp = true;
		allowableActions.clear();
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
