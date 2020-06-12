package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

/**
 * An action for instantly killing a actor
 * @author ariehendrikse
 *
 */
public class EndLifeAction extends Action {


	@Override
	public String execute(Actor actor, GameMap map) {
		map.removeActor(actor);
		return actor+ " left this world";
	}

	@Override
	public String menuDescription(Actor actor) {
		return "Quit game";
	}
	@Override
	public String hotkey() {
		return "+";
	}

}
