package game;

import edu.monash.fit2099.engine.IntrinsicWeapon;

/**
 * Class for a bite weapon.
 * 
 * @author ariehendrikse
 */
public class Bite extends IntrinsicWeapon implements Healing,HitProbability {
	
	private static final int HEAL_AMOUNT = 5;
	/**
	 * Constructor 
	 */
	public Bite() {
		super(10, "bites");
		
	}
	/**
	 * A getter for this hitpoints recovered when attacking
	 * @return the number of hitpoints recovered when this attack is performed
	 */
	public int gethealAmount() {
		return HEAL_AMOUNT;
	}
	@Override
	public int getHitProbability() {
		return 20;
	}
}
