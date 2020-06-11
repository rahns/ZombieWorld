package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.GameMap;

public class ChooseShootTargetAction extends Action implements MenuAction {

	private SniperRifle gun;

	public ChooseShootTargetAction(SniperRifle sniperRifle) {
		this.gun=sniperRifle;
	}

	@Override
	public Action getAction(Actor actor, GameMap map, Display display) {
		SubMenu sub = new SubMenu();
		for (Actor a : gun.getTargets(actor,map,gun)) {
			sub.addActionToMenu(new ShootAction(a,gun),actor,display,null);
		}
		return sub.readInput(display);
	}

	@Override
	public String execute(Actor actor, GameMap map) {
		return null;
	}
	@Override
	public String menuDescription(Actor actor) {
		return "Shoot";
	}

}
