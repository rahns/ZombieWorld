package game;

import edu.monash.fit2099.engine.WeaponItem;

/**
 * Represents an Arm and can be used as a weapon.
 * Can be crafted into a ZombieClub when in an actor's inventory.
 * 
 * @author Rahn Stavar
 *
 */
public class Arm extends Limb{

	/**
	 * Arm constructor
	 */
	public Arm() {
		super("arm", '!', 15);
	}

	/**
	 * Get an instance of the item this Arm can be crafted into, a ZombieClub
	 * 
	 * @return a new ZombieClub instance
	 */
	@Override
	public WeaponItem craftsInto() {
		return new ZombieClub();
	}
}
