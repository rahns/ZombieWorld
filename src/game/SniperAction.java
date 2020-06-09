package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.GameMap;
import edu.monash.fit2099.engine.Menu;

public class SniperAction extends Action implements MenuAction {
	SniperRifle sniper;
	Actor target;
	public SniperAction(SniperRifle sniper, Actor a) {
		this.sniper=sniper;
		this.target=a;
	}

	@Override
	public Menu getMenu(GameMap map) {
		return new SniperMenu(target,sniper);
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		return null;
	}

	@Override
	public String menuDescription(Actor actor) {
		return "Choose "+target + " " + ((ZombieActor) target).getHealthStatus();
	}

}
