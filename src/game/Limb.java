package game;

import edu.monash.fit2099.engine.WeaponItem;

public abstract class Limb extends WeaponItem{

	public Limb(String name, char displayChar, int damage) {
		super(name, displayChar, damage, "whacked");
	}

}
