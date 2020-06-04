package game;

import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.DoNothingAction;

public class DoNothingCustomMessageAction extends DoNothingAction {
	
	private String message;
	
	public DoNothingCustomMessageAction(String message) {
		this.message = message;
	}
	
	@Override
	public String menuDescription(Actor actor) {
		return actor + " " + message;
	}

}
