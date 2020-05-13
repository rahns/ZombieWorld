package game;

import edu.monash.fit2099.engine.WeaponItem;

public class Arm extends Limb{

	public Arm() {
		super("arm", '!', 15);
	}

	@Override
	public WeaponItem craftsInto() {
		return new ZombieClub();
	}
}
