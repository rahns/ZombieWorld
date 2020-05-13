package game;

import edu.monash.fit2099.engine.WeaponItem;

public class Leg extends Limb{

	public Leg() {
		super("leg", 'L', 17);
	}

	@Override
	public WeaponItem craftsInto() {
		return new ZombieMace();
	}
}
