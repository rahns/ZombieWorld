package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

public class SniperAction extends Action implements MenuAction {
	SniperRifle sniper;
	public SniperAction(SniperRifle sniper) {
		this.sniper=sniper;
	}


	@Override
	public Menu getMenu(GameMap map) {
		return new ChooseActorMenu(map);
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return "Use Sniper Rifle";
	}

}
