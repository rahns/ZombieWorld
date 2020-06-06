package game;

import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Ground;
import edu.monash.fit2099.engine.Location;

/**
 * A class representing a door that can be opened and closed by an actor
 * @author Rahn Stavar
 *
 */
public class Door extends Ground implements Toggleable {
	
	private boolean isOpen = false;

	/**
	 * Door constructor
	 */
	public Door() {
		super('_');
	}
	
	/**
	 * A method that returns the allowable actions for a door
	 * 
	 * Parameters aren't used, only present to comply with Ground's public interface
	 * 
	 * @return a ToggleAction allowing actors to open/close the door
	 */
	@Override
	public Actions allowableActions(Actor actor, Location location, String direction){
		Actions action = new Actions();
		action.add(new ToggleAction(this, isOpen));
		return action;
	}
	
	/**
	 * Toggles the door between open and closed states
	 */
	@Override
	public void toggle() {
		isOpen = !isOpen;
		if (isOpen) {
			displayChar = '|';
		}
		else {
			displayChar = '_';
		}
	}
	
	/**
	 * Lets the caller know if an actor can pass through the door
	 * @param actor not used, only present to comply with Ground's public interface
	 * @return True if the door is open, False otherwise
	 */
	@Override
	public boolean canActorEnter(Actor actor) {
		return isOpen;
	}

	/**
	 * A method to get a description of what would happen if the state was switched
	 * 
	 * @param stateToSwitchTo the state that would be switched to that the caller need a description for
	 * 
	 * @return a description of what would happen if the state was switched to stateToSwitchTo
	 */
	@Override
	public String switchToStateDescription(boolean stateToSwitchTo) {
		if (stateToSwitchTo) {
			return "opens the door";
		}
		else {
			return "closes the door";
		}
	}

}
