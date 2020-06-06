package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

public class AimAction extends Action {
	Actor target;
	SniperRifle gun;

	public AimAction(Actor actor,SniperRifle gun) {
		this.target=actor;
		this.gun=gun;
	}





	@Override
	public String execute(Actor actor, GameMap map) {
		this.gun.aim(target);
		return null;
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return null;
	}

}
