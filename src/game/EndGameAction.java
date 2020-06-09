package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

public class EndGameAction extends Action {

	@Override
	public String execute(Actor actor, GameMap map) {
		actor.hurt(1000);
		return actor+ " took the easy way out ";
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return "End it all";
	}

}
