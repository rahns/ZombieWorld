package game;

import edu.monash.fit2099.engine.IntrinsicWeapon;

public class MissableIntrinsicWeapon extends IntrinsicWeapon implements HitProbability {
	private int hitProbability;
	
	public MissableIntrinsicWeapon(int damage, String verb, int hitProbability) {
		super(damage, verb);
		this.hitProbability=hitProbability;
	}

	public int getHitProbability() {
		return hitProbability;
	}
	public void setHitProbability(int hitProbability) {
		this.hitProbability = hitProbability;
	}
}
