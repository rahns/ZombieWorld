package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * An action to toggle a Toggleable object
 * @author Rahn Stavar
 *
 */
public class ToggleAction extends Action {
	
	private Toggleable object;
	private boolean currentState;
	
	/**
	 * ToggleAction Constructor
	 * @param object a Toggleable object to toggle
	 * @param currentState a boolean representing the current state of the toggleable object
	 */
	public ToggleAction(Toggleable object, boolean currentState) {
		this.object = object;
		this.currentState = currentState;
	}

	/**
	 * A method to make the action happen. Toggles the object and returns a description of the action
	 * @param actor the actor doing the action
	 * @param map the GameMap the actor is on
	 * 
	 * @return a description of what happened
	 */
	@Override
	public String execute(Actor actor, GameMap map) {
		object.toggle();
		return menuDescription(actor);
	}

	/**
	 * A method to get the menu description for this action
	 * @param actor the actor doing this action
	 * 
	 * @return a string describing the action
	 */
	@Override
	public String menuDescription(Actor actor) {
		return actor + " " + object.switchToStateDescription(!currentState);
	}

}
