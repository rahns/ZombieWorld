package game;

import edu.monash.fit2099.engine.IntrinsicWeapon;
/**
 * An IntrinsicWeapon that defines its chance of missing. Implements hitprobability.
 * @author ariehendrikse
 *
 */
public abstract class MissableIntrinsicWeapon extends IntrinsicWeapon implements HitProbability {
	
	private int hitProbability;
	
	/**
	 * Constructor
	 * @param damage the amount of damage this weapon deals
	 * @param verb the weapons's verb
	 * @param hitProbability the probability the weapons attack hits the target
	 */
	public MissableIntrinsicWeapon(int damage, String verb, int hitProbability) {
		super(damage, verb);
		this.hitProbability=hitProbability;
	}

	/**
	 * A getter for the weapon's hit probability
	 * @return the weapon's hit probability
	 */
	@Override
	public int getHitProbability() {
		return hitProbability;
	}
}
