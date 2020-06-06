package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

public class AimAction extends Action implements MenuAction {
	Actor actor;

	public AimAction(Actor actor) {
		this.actor=actor;
	}

	@Override
	public Menu getMenu() {
		// TODO Auto-generated method stub
		return new AimMenu(actor);
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return null;
	}

}
