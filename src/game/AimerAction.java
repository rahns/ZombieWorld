package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;

public class AimerAction extends Action {

	private Actor target;
	private SniperRifle gun;

	public AimerAction(Actor a, SniperRifle gun) {
		this.gun=gun;
		this.target=a;
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		this.gun.aim(target);
		return actor+" aimed at " + target;
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return null;
	}

}
