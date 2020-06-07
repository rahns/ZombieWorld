package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

public class ChooseActorAction extends Action implements MenuAction {
	
	SniperRifle sniper;

	public ChooseActorAction(SniperRifle sniperRifle) {
		this.sniper=sniperRifle;
	}

	@Override
	public Menu getMenu(GameMap map) {
		// TODO Auto-generated method stub
		return new ChooseActorMenu(map,sniper);
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String menuDescription(Actor actor) {
		// TODO Auto-generated method stub
		return "Uses Sniper Rifle";
	}

}
