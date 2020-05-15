package game;

import edu.monash.fit2099.engine.WeaponItem;

/**
 * Represents a Leg and can be used as a weapon.
 * Can be crafted into a ZombieMace when in an actor's inventory.
 * 
 * @author Rahn Stavar
 *
 */
public class Leg extends Limb{

	/**
	 * Leg constructor
	 */
	public Leg() {
		super("leg", 'L', 17);
	}

	/**
	 * Get an instance of the item this Leg can be crafted into, a ZombieMace
	 * 
	 * @return a new ZombieMace instance
	 */
	@Override
	public WeaponItem craftsInto() {
		return new ZombieMace();
	}
}
