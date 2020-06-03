package game;

import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;

public class Door extends Ground {
	
	private boolean isOpen = false;

	public Door() {
		super('_');
	}
	
	@Override
	public Actions allowableActions(Actor actor, Location location, String direction){
		Actions action = new Actions();
		action.add(new DoorInteractAction(this, isOpen));
		return action;
	}
	
	public void interact() {
		isOpen = !isOpen;
		if (isOpen) {
			displayChar = '|';
		}
		else {
			displayChar = '_';
		}
	}
	
	@Override
	public boolean canActorEnter(Actor actor) {
		return isOpen;
	}
}
