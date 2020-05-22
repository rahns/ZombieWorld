package game;
/**
 * Interface for any object that is capable of healing an actor
 * @author ariehendrikse
 *
 */
public interface Healing {
	
	/**
	 * The default amount to heal for an object that implements healing but uses the default 
	 */
	public static final int DEFAULT_HEAL_AMOUNT = 10;
	
	/**
	 * Getter for how much the object heals.
	 * @return int of the hitpoints recovered when healing
	 */
	public default int getHealAmount() {
		return DEFAULT_HEAL_AMOUNT;
	}
}
