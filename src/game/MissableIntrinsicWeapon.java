package game;

import edu.monash.fit2099.engine.IntrinsicWeapon;

public class MissableIntrinsicWeapon extends IntrinsicWeapon {
	int hitProbability;
	public MissableIntrinsicWeapon(int damage, String verb, int hitProbability) {
		super(damage, verb);
		this.hitProbability=hitProbability;
	}

}
