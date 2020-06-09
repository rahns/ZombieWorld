package game;

import java.util.Iterator;

import edu.monash.fit2099.engine.Action;
import edu.monash.fit2099.engine.Actions;
import edu.monash.fit2099.engine.Actor;
import edu.monash.fit2099.engine.Display;
import edu.monash.fit2099.engine.Item;

public class SniperMenu extends SubMenu {
	private Actor target;
	private SniperRifle gun;
	public SniperMenu(Actor target, SniperRifle gun) {
		this.target=target;
		this.gun=gun;
	}

	public Action showMenu(Actor actor, Actions actions, Display display) {
		String ammo_amount="";
		for (int i = 0;i< gun.getAmmo().getBulletCount();i++) {
			ammo_amount+=">";
		}
		
		display.println("Ammo: " +ammo_amount);

		Iterator<Item> iter= actor.getInventory().iterator();
		while (iter.hasNext()) {
			Item item = iter.next();
			if (item instanceof AmmunitionCartridge) {
				addActionToMenu(new ReloadAction((AmmunitionCartridge) item,gun), actor, display, null);
			}
		}
		
		addActionToMenu(new AimAction(target,gun), actor, display, null);
		if (!(gun.getAmmo().isEmpty())) {
			addActionToMenu(new ShootAction(target,gun), actor, display, null);
		}

		return readInput(display);
	}

}
