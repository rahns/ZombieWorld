package game;
/**
 * Class for a bite weapon.
 * 
 * @author ariehendrikse
 */

public class Bite extends MissableIntrinsicWeapon implements Healing {
	
	private static final int HEAL_AMOUNT = 5;
	/**
	 * Constructor 
	 */
	public Bite() {
		super(10, "bites", 10);
	}
	/**
	 * A getter for this  hitpoints recovered when attacking
	 * @return the number of hitpoints recovered when this attack is performed
	 */
	public int gethealAmount() {
		return HEAL_AMOUNT;
	}
}
