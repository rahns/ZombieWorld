package game;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;

public class SniperMenu extends SubMenu {
	private Actor target;
	private SniperRifle gun;
	public SniperMenu(Actor target, SniperRifle gun) {
		this.target=target;
		this.gun=gun;
	}

	public Action showMenu(Actor actor, Actions actions, Display display) {
		
		//TODO math on what locations are to be hit
		
		addActionToMenu(new AimAction(target,gun), actor, display, '1');
		addActionToMenu(new ShootAction(target,gun), actor, display, '2');

		return readInput(display);
	}

}
