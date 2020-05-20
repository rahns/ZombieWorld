package game;

import edu.monash.fit2099.engine.WeaponItem;

/**
 * A primitive weapon.
 * 
 * @author ram
 *
 */
public class Plank extends WeaponItem {

	public Plank() {
		super("plank", ')', 40, "whacks");
		// Plank is rare and a long way from where the player spawns, so increased its damage to make the trip worth it
	}

}
