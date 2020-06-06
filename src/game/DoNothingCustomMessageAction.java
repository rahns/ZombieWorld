package game;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.DoNothingAction;

/**
 * An action to do nothing, with a custom description of that action
 * @author Rahn Stavar
 *
 */
public class DoNothingCustomMessageAction extends DoNothingAction {
	
	private String message;
	
	/**
	 * DoNothingCustomMessageAction constructor
	 * 
	 * @param message the custom message to be displayed when this action is executed
	 */
	public DoNothingCustomMessageAction(String message) {
		this.message = message;
	}
	
	/**
	 * Returns the description of the action
	 * @param actor the actor doing this action
	 * @return the description of this action
	 */
	@Override
	public String menuDescription(Actor actor) {
		return actor + " " + message;
	}

}
