package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

public class DoorInteractAction extends Action {
	
	private Door door;
	private boolean isOpen;
	
	public DoorInteractAction(Door door, boolean isOpen) {
		this.door = door;
		this.isOpen = isOpen;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		door.interact();
		return menuDescription(actor);
	}

	@Override
	public String menuDescription(Actor actor) {
		if (isOpen) {
			return actor + " closes the door";
		}
		else {
			return actor + " opens the door";
		}
	}

}
