package game;

import edu.monash.fit2099.engine.IntrinsicWeapon;
/**
 * INtrinsicWeapon that has the chance of missing. Implements hitprobability.
 * @author ariehendrikse
 *
 */
public class MissableIntrinsicWeapon extends IntrinsicWeapon implements HitProbability {
	protected int hitProbability;
	
	public MissableIntrinsicWeapon(int damage, String verb, int hitProbability) {
		super(damage, verb);
		this.hitProbability=hitProbability;
	}


	
	@Override
	public int getHitProbability() {
		return hitProbability;
	}
}
